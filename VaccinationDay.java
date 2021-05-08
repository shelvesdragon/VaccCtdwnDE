import java.time.LocalDate;

public class VaccinationDay {
  // TODO sort through needed and unneeded data, decide on first doses
  LocalDate date;
  int dosesCumulative;
  int dosesDifferenceToPrevDay;
  int dosesFirstDifferenceToPrevDay;
  int dosesSecondDifferenceToPrevDay;
  int dosesBiontechCumulative;
  int dosesModernaCumulative;
  int dosesAstraZenecaCumulative;
  int personsFirstCumulative;
  int personsFullCumulative;
  double vaccinationRateFirst;
  double vaccinationRateFull;
  int indicationAgeDoses;
  int indicationProfessionDoses;
  int indicationMedicalDoses;
  int indicationCareHomeDoses;
  int indicationAgeFirst;
  int indicationProfessionFirst;
  int indicationMedicalFirst;
  int indicationCareHomeFirst;
  int indicationAgeFull;
  int indicationProfessionFull;
  int indicationMedicalFull;
  int indicationCareHomeFull;
  int dosesDimCumulative;
  int dosesKbvCumulative;
  int dosesJohnsonCumulative;
  int dosesBiontechFirstCumulative;
  int dosesBiontechSecondCumulative;
  int dosesModernaFirstCumulative;
  int dosesModernaSecondCumulative;
  int dosesAstraZenecaFirstCumulative;
  int dosesAstraZenecaSecondCumulative;


  VaccinationDay(LocalDate dat, int dosesCumul) {
    this.date = dat;
    this.dosesCumulative = dosesCumul;
  }

  @Override
  public String toString() {
    // TODO
    return "{" + this.date + ", dc: " + this.dosesCumulative + "}";
  }
}