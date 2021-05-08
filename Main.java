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
  public static void main(String[] args) throws IOException {
    // get the .tsv from RKI
    String vaccinationTimeSeriesURLString = "https://impfdashboard.de/static/data/germany_vaccinations_timeseries_v2.tsv";
    String vaccinationTimeSeriesFileNameString = "res/germany_vaccinations_timeseries_v2.tsv";
    String vaccinesFileNameString = "res/vaccines.csv";
    try {
      URL vaccinationTimeSeriesURL = new URL(vaccinationTimeSeriesURLString);
      HttpsURLConnection httpsConnection = (HttpsURLConnection)vaccinationTimeSeriesURL.openConnection();
      try {
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpsConnection.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new FileWriter(vaccinationTimeSeriesFileNameString));
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
        System.err.println("Error in reading and writing timeseries, check URL and fileName");
        e.printStackTrace();
      }
    } catch (MalformedURLException e) {
      System.err.println("Error in vaccination timeseries URL");
      e.printStackTrace();
    }
    
    ArrayList<Vaccine> listOfVaccines = readVaccines(vaccinesFileNameString);
    for (Vaccine vaccine : listOfVaccines) {
      System.out.println(vaccine);
    }
    // TODO define a way to calculate the full protection of each vaccine
    ArrayList<VaccinationDay> listOfDays = readVaccinationDays(vaccinationTimeSeriesFileNameString);
    System.out.println(listOfDays.get(listOfDays.size() - 1));
    // TODO number of cumulated second doses
    // TODO number of fully protected
    // TODO % of whole population
    // TODO number of immunity in 70 % of population
    // TODO date/time until 70% population gets second dose
    // TODO date/time until 70% population is fully protected, weighted against current percentage of vaccines
    // TODO date/time until whole population gets second dose
    // TODO date/time until whole population is fully protected, weighted against current percentage of vaccines
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
      System.out.println(dayDateFromString);
      int dosesCumul = Integer.parseInt(dayStringArray[1]);
      System.out.println(dayStringArray[1]);
      System.out.println();
      vaccDay = new VaccinationDay(dayDateFromString, dosesCumul);
      listOfDays.add(vaccDay);
      dayString = reader.readLine();
    }
    reader.close();
    return listOfDays;
  }
}
