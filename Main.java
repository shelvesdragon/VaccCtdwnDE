import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class Main {
  private static final double MINIMUM_HERD_IMMUNITY_DECIMAL = 0.70;
  private static final String PERCENTAGE_OF_FULLY_VACCINATED_PERSONS = "Percentage of fully vaccinated persons: ";
  private static final int POPULATION_GERMANY = 83190556; // https://www.destatis.de/DE/Themen/Gesellschaft-Umwelt/Bevoelkerung/Bevoelkerungsstand/Tabellen/zensus-geschlecht-staatsangehoerigkeit-2020.html
  private static final String FULLY_VACCINATED_PERSONS = "Fully vaccinated persons: ";
  private static final String VACCINES_CSV_FILENAME = "res/vaccines.csv";
  private static final String TIMESERIES_FILENAME = "res/germany_vaccinations_timeseries_v2.tsv";
  private static final String TIMESERIES_URL = "https://impfdashboard.de/static/data/germany_vaccinations_timeseries_v2.tsv";
  private static final String ERROR_IN_READING_AND_WRITING_TIMESERIES_CHECK_URL_AND_FILENAME = "Error in reading and writing timeseries, check URL and fileName";
  private static final String ERROR_IN_VACCINATION_TIMESERIES_URL = "Error in vaccination timeseries URL";
  private static final String TIMESERIES_DOWNLOAD_SUCCESSFUL = "Timeseries download successful.";

  public static void main(String[] args) throws IOException {
    
    // get the .tsv from RKI
    double minHerdImmunityDecimal = 0.70; // = 70% basic number, no dispersion, mutation, etc. according to https://www.quarks.de/gesundheit/medizin/warum-ein-impfstoff-die-pandemie-auch-2021-nicht-beendet/
    /* https://www.quarks.de/gesundheit/medizin/warum-ein-impfstoff-die-pandemie-auch-2021-nicht-beendet/
     * Please take a close look at this article, because 70% is technically too low and doesn't take important effects into account.
     * The article explains them and also why herd immunity is probably not reachable for COVID-19 and/or will take a longer time.
     * It also explains why vaccines are still important to get:
     * Protect yourself and others against death, Long-COVID, hospitalisation, etc.
     * Again, this is not a predicition, it is only calculated from past performance.
     * 
     * This does not take willingness of population to get the vaccine into account:
     * https://www.rki.de/DE/Content/InfAZ/N/Neuartiges_Coronavirus/Projekte_RKI/covimo_studie_Ergebnisse.html
     * since no data for population below the age of 18 is available and vaccines are available for ages 16 and up (2021-08-05)
     */
    if (args.length == 0 || !(args[0].equals("noTimeSeriesUpdate"))) {
      getTimeSeriesFromRKI();
    }
    
    ArrayList<Vaccine> listOfVaccines = readVaccines(VACCINES_CSV_FILENAME);
    for (Vaccine vaccine : listOfVaccines) {
      System.out.println(vaccine);
    }
    /* A person is fully vaccinated when:
    * - they got their last dose
    * A person is fully protected when:
    * - after they got their last dose and
    * - the days until the full/last data point in research on efficacy have passed
    * - a fully protected person may not be 100 % protected against the virus, due to the vaccines efficacy <100%, but
    * - the vaccine is likely to have reached its full potential
    * The number of fully vaccinated persons is easily read from the last row in the timeseries
    * The last number of fully protected persons is the number of last doses of each vaccine:
    * Sum of last doses of row number (last row minus days after last dose was given), for each vaccine.
    * Consider vaccine A and vaccine B in with number of last doses in last row 27.
    * Let vaccine A take 5 days to develop its full protection after the last dose.
    * Let vaccine B take 7 days to develop its full protection after the last dose.
    * Then the number x of all fully protected persons for A is in row 27 - 5 = 22.
    * The number y of all fully protected persons for B is in row 27 - 7 = 20.
    * The number of all fully protected persons for A and B is x + y.
    * Since it takes different numbers of doses to get the full protection of a vaccine for different vaccines,
    * the overall numbers of doses given does not say much about how many persons have a full protection.
    * It may be more important when looking at delivered doses vs. vaccinated doses and planning ahead.
    * The number of first doses in vaccines with two or more do not matter as much to determine protection of population.
    * While persons with one or more of several does of their vaccine may already have some protection, efficacy varies between
    * different vaccines. It may however be a good number for planning and determining requirements of needed second & additional doses.
    * Therefore, the important numbers are the last doses of each vaccine and the days until full protection is reached.
    */
    // TODO implement number of fully protected persons
    ArrayList<VaccinationDay> listOfDays = readVaccinationDays(TIMESERIES_FILENAME);
    // number of fully vaccinated persons
    int cumulatedFullyVaccinatedPersons = listOfDays.get(listOfDays.size() - 1).personsFullCumulative;
    System.out.println(FULLY_VACCINATED_PERSONS + cumulatedFullyVaccinatedPersons);
    // TODO number of fully protected
    // bio pf = 6059737
    // astra = 16986
    // jannsen = 0
    // moderna = 339542
    // sum = 6416265
    int maxFullyProtectedPersons = 0;
    int minFullyProtectedPersons = 0;
    for (int i = 0; i < listOfVaccines.size(); i++) {
      // maximum full portection through minimum days to go back
      int maxProtectionDays = (listOfDays.size() - 1) - listOfVaccines.get(i).getMinimumDaysFullProtection();
      int minProtectionDays = (listOfDays.size() - 1) - listOfVaccines.get(i).getMaximumDaysFullProtection();
      int colummnNumber = listOfVaccines.get(i).getColumnNumberSecondDoses() - 1; // indexOffset and date
      if (maxProtectionDays >= 0) {
        if (colummnNumber < 9) {
          maxFullyProtectedPersons += listOfDays.get(maxProtectionDays).firstHalf[colummnNumber];
        } else {
          maxFullyProtectedPersons += listOfDays.get(maxProtectionDays).secondHalf[colummnNumber - 11];
        }
      }
      if (minProtectionDays >= 0) {
        if (colummnNumber < 9) {
          minFullyProtectedPersons += listOfDays.get(minProtectionDays).firstHalf[colummnNumber];
        } else {
          minFullyProtectedPersons += listOfDays.get(minProtectionDays).secondHalf[colummnNumber - 11];
        }
      }
    }
    System.err.println(maxFullyProtectedPersons);
    System.err.println(minFullyProtectedPersons);
    // TODO % of whole population (fully protected)
    double percentageFullyVaccinatedPersons = 100.0 * ((double)cumulatedFullyVaccinatedPersons / POPULATION_GERMANY);
    System.out.println(PERCENTAGE_OF_FULLY_VACCINATED_PERSONS + percentageFullyVaccinatedPersons);
    // TODO number of immunity in 70 % of population
    // TODO date/time until 70% population gets second dose
    // TODO date/time until 70% population is fully protected, weighted against current percentage of vaccines
    // TODO date/time until whole population gets second dose
    // TODO date/time until whole population is fully protected, weighted against current percentage of vaccines and with longest 
    /*
    * # total doeses already vaccinated
    col=2 # in this collumn
    cut -f$col $FILE > "res/ctout1.txt"
    totalDosesVaccinated=$(tail -n1 res/ctout1.txt)
    # doses to be vaccinated in total
    totalDosesToBeVaccinated=$((2 * $POPULATIONGER))
    # doses to be vaccinated of willing percentage
    willingDosesToBeVaccinated=$((2 * $WILLINGPERSONSPERC * $POPULATIONGER / 100))
    # doses to be vaccinated immunity percentage
    immunityDosesToBeVaccinated=$((2 * $IMMUNITYPERC * $POPULATIONGER / 100))
    # doses to go total (no difference made between first and second)
    totalDosesToGo=$(($totalDosesToBeVaccinated - $totalDosesVaccinated))
    # doses to go until willing population is fully vaccinated (no diff between first and second)
    willingDosesToGo=$(($willingDosesToBeVaccinated - $totalDosesVaccinated))
    # doses to go until certain immunity in population is fully vaccinated
    immunityDosesToGo=$(($immunityDosesToBeVaccinated - $totalDosesVaccinated))
    # percentage of total doses
    totalPerc=$(($(($totalDosesVaccinated * 100)) / $totalDosesToBeVaccinated))
    
    # Some output inbetween
    printf "$totalDosesVaccinated\tdoses have been vaccinated in total ($totalPerc %%)\n"
    printf "$totalDosesToGo\tdoses until all doses vaccinated (no difference in first or second)\n"
    printf "$willingDosesToGo\tdoses until willing $WILLINGPERSONSPERC %% is fully vaccinated (~)\n"
    printf "$immunityDosesToGo\tdoses until level of $IMMUNITYPERC %% is fully vaccinated (~)\n"
    echo ""
    
    # total of persons with first vaccination
    col=9 # in this collumn
    cut -f$col $FILE > "res/ctout2.txt"
    totalFirstVaccinated=$(tail -n1 res/ctout2.txt)
    # first doses to be vaccinated in total
    totalFirstToBeVaccinated=$POPULATIONGER
    # first doses to be vaccinated of willing percentage
    willingFirstToBeVaccinated=$(($WILLINGPERSONSPERC * $POPULATIONGER / 100))
    # first doses to be vaccinated immunity percentage
    immunityFirstToBeVaccinated=$(($IMMUNITYPERC * $POPULATIONGER / 100))
    # first doses to go total
    totalFirstToGo=$(($totalFirstToBeVaccinated - $totalFirstVaccinated))
    # first doses to go until willing population is first vaccinated
    willingFirstToGo=$(($willingFirstToBeVaccinated - $totalFirstVaccinated))
    # first doses to go until certain immunity in population is first vaccinated
    immunityFirstToGo=$(($immunityFirstToBeVaccinated - $totalFirstVaccinated))
    # percentage of total first dose
    firstPerc=$(($(($totalFirstVaccinated * 100)) / $totalFirstToBeVaccinated))
    
    # Some output inbetween
    printf "$totalFirstVaccinated\tpersons got their first vaccination so far ($firstPerc %%)\n"
    printf "$totalFirstToGo\tdoses until whole population got their first dose\n"
    printf "$willingFirstToGo\tdoses until willing $WILLINGPERSONSPERC %% got their first dose\n"
    printf "$immunityDosesToGo\tdoses until level of $IMMUNITYPERC %% got their first dose\n"
    echo ""
    
    # total of persons with both vaccinations
    col=10 # in this collumn
    cut -f$col $FILE > "res/ctout3.txt"
    totalBothVaccinated=$(tail -n1 res/ctout3.txt)
    # both doses to be vaccinated in total
    totalBothToBeVaccinated=$POPULATIONGER
    # both doses to be vaccinated of willing percentage
    willingBothToBeVaccinated=$(($WILLINGPERSONSPERC * $POPULATIONGER / 100))
    # both doses to be vaccinated immunity percentage
    immunityBothToBeVaccinated=$(($IMMUNITYPERC * $POPULATIONGER / 100))
    # both doses to go total (only second doses taken into account)
    totalBothToGo=$(($totalBothToBeVaccinated - $totalBothVaccinated))
    # both doses to go until willing population is fully vaccinated (only second doses taken into account)
    willingBothToGo=$(($willingBothToBeVaccinated - $totalBothVaccinated))
    # both doses to go until certain immunity in population is fully vaccinated
    immunityBothToGo=$(($immunityBothToBeVaccinated - $totalBothVaccinated))
    # percentage of total second dose
    bothPerc=$(($(($totalBothVaccinated * 100)) / $totalBothToBeVaccinated))
    
    # Some output inbetween
    printf "$totalBothVaccinated\tpersons got their first and their second vaccination so far\n"
    printf "$totalBothToGo\tdoses until whole population got their second dose (only)\n"
    printf "$willingBothToGo\tdoses until willing $WILLINGPERSONSPERC %% got their first dose\n"
    printf "$immunityBothToGo\tdoses until level of $IMMUNITYPERC %% got their first dose\n"
    echo ""
    
    # Averages
    # average doses per day over $DAYS
    col=3 # collumn for new doses both first and second on a single day
    cut -f$col $FILE > "res/ctout4.txt"
    tail -n$DAYS "res/ctout4.txt" > "res/diff1.txt" # get last $DAYS values
    avgTotalDoses=0
    # average first vaccinations per day over $DAYS
    col=4 # collumn for new doses (first) on a single day
    cut -f$col $FILE > "res/ctout5.txt"
    tail -n$DAYS "res/ctout5.txt" > "res/diff2.txt" # get last $DAYS values
    avgFirstDoses=0
    # average second vaccinations per day over $DAYS
    col=5 # collumn for new doses (second) on a single day
    cut -f$col $FILE > "res/ctout6.txt"
    tail -n$DAYS "res/ctout6.txt" > "res/diff3.txt" # get last $DAYS values
    avgSecondDoses=0
    # calculate all averages
    for (( i=1; i<=$DAYS; i++ ))
    do
    avgTotalDoses=$(( $avgTotalDoses + $(head -$i res/diff1.txt | tail -1) / $DAYS ))
    avgFirstDoses=$(( $avgFirstDoses + $(head -$i res/diff2.txt | tail -1) / $DAYS ))
    avgSecondDoses=$(( $avgSecondDoses + $(head -$i res/diff3.txt | tail -1) / $DAYS ))
    done
    
    # Some more output inbetween
    printf "$avgTotalDoses\tdoses were vaccinated per day on average over $DAYS days\n"
    printf "$avgFirstDoses\tpersons got their first dose per day on average over $DAYS days\n"
    printf "$avgSecondDoses\tpersons got their second dose per day on average over $DAYS days\n"
    echo ""
    
    # calculated days
    # days for 7 day averages on total doses (both first and second doses)
    daysToGoTotalVaccinated=$(($totalDosesToGo / $avgTotalDoses))
    daysToGoTotalWillingVaccinated=$(($willingDosesToGo / $avgTotalDoses))
    daysToGoTotalImmunityVaccinated=$(($immunityDosesToGo / $avgTotalDoses))
    printf "$daysToGoTotalVaccinated\tdays to go all population (no difference between first or second made)\n"
    printf "$daysToGoTotalWillingVaccinated\tdays to go for willing $WILLINGPERC (no difference between first or second made)\n"
    printf "$daysToGoTotalImmunityVaccinated\tdays to go for immunity is in $IMMUNITYPERC (no difference between first or second made)\n"
    echo ""
    # days for 7 day averages on first doses
    daysToGoFirstVaccinated=$(($totalFirstToGo / $avgFirstDoses))
    daysToGoFirstWillingVaccinated=$(($willingFirstToGo / $avgFirstDoses))
    daysToGoFirstImmunityVaccinated=$(($immunityFirstToGo / $avgFirstDoses))
    printf "$daysToGoFirstVaccinated\tdays to go all population got their first dose\n"
    printf "$daysToGoFirstWillingVaccinated\tdays to go for willing $WILLINGPERC got their first dose\n"
    printf "$daysToGoFirstImmunityVaccinated\tdays to go for immunity $IMMUNITYPERC got their first dose\n"
    echo ""
    # days for 7 day averages on second doses
    daysToGoBothVaccinated=$(($totalBothToGo / $avgSecondDoses))
    daysToGoBothWillingVaccinated=$(($willingBothToGo / $avgSecondDoses))
    daysToGoBothImmunityVaccinated=$(($immunityBothToGo / $avgSecondDoses))
    printf "$daysToGoBothVaccinated\tdays to go all population got their second dose (counting second doses)\n"
    printf "$daysToGoBothWillingVaccinated\tdays to go for willing $WILLINGPERC got their second dose (counting second doses)\n"
    printf "$daysToGoBothImmunityVaccinated\tdays to go for immunity $IMMUNITYPERC got their second dose (counting second doses)\n"
    echo ""
    
    # dates to the days above
    dateToGoTotalVaccinated=$(date --date=@$(($TODAYSEC + 86400 * $daysToGoTotalVaccinated)))
    dateToGoTotalWillingVaccinated=$(date --date=@$(($TODAYSEC + 86400 * $daysToGoTotalWillingVaccinated)))
    dateToGoTotalImmunityVaccinated=$(date --date=@$(($TODAYSEC + 86400 * $daysToGoTotalImmunityVaccinated)))
    printf "as viewed by total doses, all population will be vaccinated at:\t$dateToGoTotalVaccinated\n"
    printf "as viewed by total doses, willing $WILLINGPERSONSPERC %% will be vaccinated at:\t$dateToGoTotalWillingVaccinated\n"
    printf "as viewed by total doses, immunity $IMMUNITYPERC %% will be vaccinated at:\t$dateToGoTotalImmunityVaccinated\n"
    echo ""
    dateToGoFirstVaccinated=$(date --date=@$(($TODAYSEC + 86400 * $daysToGoFirstVaccinated)))
    dateToGoFirstWillingVaccinated=$(date --date=@$(($TODAYSEC + 86400 * $daysToGoFirstWillingVaccinated)))
    dateToGoFirstImmunityVaccinated=$(date --date=@$(($TODAYSEC + 86400 * $daysToGoFirstImmunityVaccinated)))
    printf "as viewed by first doses, all population will be vaccinated at:\t$dateToGoFirstVaccinated\n"
    printf "as viewed by first doses, willing $WILLINGPERSONSPERC %% will be vaccinated at:\t$dateToGoFirstWillingVaccinated\n"
    printf "as viewed by first doses, immunity $IMMUNITYPERC %% will be vaccinated at:\t$dateToGoFirstImmunityVaccinated\n"
    echo ""
    dateToGoBothVaccinated=$(date --date=@$(($TODAYSEC + 86400 * $daysToGoBothVaccinated)))
    dateToGoBothWillingVaccinated=$(date --date=@$(($TODAYSEC + 86400 * $daysToGoBothWillingVaccinated)))
    dateToGoBothImmunityVaccinated=$(date --date=@$(($TODAYSEC + 86400 * $daysToGoBothImmunityVaccinated)))
    printf "as viewed by second doses, all population will be vaccinated at:\t$dateToGoBothVaccinated\n"
    printf "as viewed by second doses, willing $WILLINGPERSONSPERC %% will be vaccinated at:\t$dateToGoBothWillingVaccinated\n"
    printf "as viewed by second doses, immunity $IMMUNITYPERC %% will be vaccinated at:\t$dateToGoBothImmunityVaccinated\n"
    echo ""
    
    */
  }
  
  private static void getTimeSeriesFromRKI() throws IOException {
    try {
      URL vaccinationTimeSeriesURL = new URL(TIMESERIES_URL);
      HttpsURLConnection httpsConnection = (HttpsURLConnection)vaccinationTimeSeriesURL.openConnection();
      try {
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpsConnection.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new FileWriter(TIMESERIES_FILENAME));
        String lineURLToFile = reader.readLine();
        while(lineURLToFile!= null) {
          writer.write(lineURLToFile);
          writer.newLine();
          lineURLToFile = reader.readLine();
        }
        writer.flush();
        reader.close();
        writer.close();
      } catch (IOException e) {
        System.err.println(ERROR_IN_READING_AND_WRITING_TIMESERIES_CHECK_URL_AND_FILENAME);
        e.printStackTrace();
      }
    } catch (MalformedURLException e) {
      System.err.println(ERROR_IN_VACCINATION_TIMESERIES_URL);
      e.printStackTrace();
    }
    System.err.println(TIMESERIES_DOWNLOAD_SUCCESSFUL);
  }

  public static ArrayList<Vaccine> readVaccines(String vaccineFileName) throws IOException {
    ArrayList<Vaccine> listOfVaccines = new ArrayList<Vaccine>();
    BufferedReader reader = new BufferedReader(new FileReader(vaccineFileName));
    String lineCSV = reader.readLine();
    lineCSV = reader.readLine();
    String[] lineArray;
    String vacName;
    String producer;
    String vacType;
    int columnNoScndDoses;
    int doses;
    int maxFull;
    int minFull;
    String[] dayStringArray;
    int[] maxDaysBtwnDoses;
    int[] minDaysBtwnDoses;
    Vaccine tempVaccine;
    while (lineCSV != null) {
      System.err.println(lineCSV);
      lineArray = lineCSV.split(",");
      vacName = lineArray[0];
      producer = lineArray[1];
      vacType = lineArray[2];
      columnNoScndDoses = Integer.parseInt(lineArray[3]);
      doses = Integer.parseInt(lineArray[4]);
      maxFull = Integer.parseInt(lineArray[5]);
      minFull = Integer.parseInt(lineArray[6]);
      dayStringArray = lineArray[7].split("-");
      maxDaysBtwnDoses = new int[dayStringArray.length];
      for (int i = 0; i < maxDaysBtwnDoses.length; i++) {
        maxDaysBtwnDoses[i] = Integer.parseInt(dayStringArray[i]);
      }
      dayStringArray = lineArray[8].split("-");
      minDaysBtwnDoses = new int[dayStringArray.length];
      for (int i = 0; i < minDaysBtwnDoses.length; i++) {
        minDaysBtwnDoses[i] = Integer.parseInt(dayStringArray[i]);
      }
      tempVaccine = new Vaccine(vacName, producer, vacType, columnNoScndDoses, doses, maxFull, minFull, maxDaysBtwnDoses, minDaysBtwnDoses);
      listOfVaccines.add(tempVaccine);
      lineCSV = reader.readLine();
    }
    reader.close();
    return listOfVaccines;
  }
  
  // TODO define File loader according to needed Values
  public static ArrayList<VaccinationDay> readVaccinationDays (String vaccineDayFileName) throws IOException {
    ArrayList<VaccinationDay> listOfDays = new ArrayList<VaccinationDay>();
    BufferedReader reader = new BufferedReader(new FileReader(vaccineDayFileName));
    String dayString = reader.readLine();
    dayString = reader.readLine();
    String[] dayStringArray;
    VaccinationDay vaccDay;
    while (dayString != null) {
      dayStringArray = dayString.split("\t");
      LocalDate dayDateFromString = LocalDate.parse(dayStringArray[0]);
      // System.err.println(dayDateFromString);
      int dosesCumul = Integer.parseInt(dayStringArray[1]);
      // System.err.println(dayStringArray[1]);
      // System.err.println();
      int dosesDiffPrev = Integer.parseInt(dayStringArray[2]);
      int dosesFirstDiffPrev = Integer.parseInt(dayStringArray[3]);
      int dosesSecondDiffPrev = Integer.parseInt(dayStringArray[4]);
      int dosesBiontechCumul = Integer.parseInt(dayStringArray[5]);
      int dosesModernaCumul = Integer.parseInt(dayStringArray[6]);
      int dosesAstraZenecaCumul = Integer.parseInt(dayStringArray[7]);
      int personsFirstCumul = Integer.parseInt(dayStringArray[8]);
      int personsFullCumul = Integer.parseInt(dayStringArray[9]);
      double vaccRateFirst = Double.parseDouble(dayStringArray[10]);
      double vaccRateFull = Double.parseDouble(dayStringArray[11]);
      int indicAgeDoses = Integer.parseInt(dayStringArray[12]);
      int indicProfDoses = Integer.parseInt(dayStringArray[13]);
      int indicMedicalDoses = Integer.parseInt(dayStringArray[14]);
      int indicCareHomeDoses = Integer.parseInt(dayStringArray[15]);
      int indicAgeFirst = Integer.parseInt(dayStringArray[16]);
      int indicProfFirst = Integer.parseInt(dayStringArray[17]);
      int indicMedicalFirst = Integer.parseInt(dayStringArray[18]);
      int indicCareHomeFirst = Integer.parseInt(dayStringArray[19]);
      int indicAgeFull = Integer.parseInt(dayStringArray[20]);
      int indicProfFull = Integer.parseInt(dayStringArray[21]);
      int indicMedicalFull = Integer.parseInt(dayStringArray[22]);
      int indicCareHomeFull = Integer.parseInt(dayStringArray[23]);
      int dosesDimCumul = Integer.parseInt(dayStringArray[24]);
      int dosesKbvCumul = Integer.parseInt(dayStringArray[25]);
      int dosesJohnsonCumul = Integer.parseInt(dayStringArray[26]);
      int dosesBiontechFirstCumul = Integer.parseInt(dayStringArray[27]);
      int dosesBiontechSecondCumul = Integer.parseInt(dayStringArray[28]);
      int dosesModernaFirstCumul = Integer.parseInt(dayStringArray[29]);
      int dosesModernaSecondCumul = Integer.parseInt(dayStringArray[30]);
      int dosesAstraZenecaFirstCumul = Integer.parseInt(dayStringArray[31]);
      int dosesAstraZenecaSecondCumul = Integer.parseInt(dayStringArray[32]);
      vaccDay = new VaccinationDay(dayDateFromString, dosesCumul, dosesDiffPrev, dosesFirstDiffPrev,
          dosesSecondDiffPrev, dosesBiontechCumul, dosesModernaCumul, dosesAstraZenecaCumul,
          personsFirstCumul, personsFullCumul, vaccRateFirst, vaccRateFull, indicAgeDoses,
          indicProfDoses, indicMedicalDoses, indicCareHomeDoses, indicAgeFirst, indicProfFirst,
          indicMedicalFirst, indicCareHomeFirst, indicAgeFull, indicProfFull, indicMedicalFull,
          indicCareHomeFull, dosesDimCumul, dosesKbvCumul, dosesJohnsonCumul, dosesBiontechFirstCumul,
          dosesBiontechSecondCumul, dosesModernaFirstCumul, dosesModernaSecondCumul, dosesAstraZenecaFirstCumul,
          dosesAstraZenecaSecondCumul);
      listOfDays.add(vaccDay);
      dayString = reader.readLine();
    }
    reader.close();
    return listOfDays;
  }
}
