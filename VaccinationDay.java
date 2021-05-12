import java.time.LocalDate;

public class VaccinationDay {
  // TODO sort through needed and unneeded data, redo into field or String
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
  int[] firstHalf;
  int[] secondHalf;

  VaccinationDay(LocalDate dat, int dosesCumul, int dosesDiffPrev, int dosesFirstDiffPrev, int dosesSecondDiffPrev,
      int dosesBiontechCumul, int dosesModernaCumul, int dosesAstraZenecaCumul, int personsFirstCumul,
      int personsFullCumul, double vaccRateFirst, double vaccRateFull, int indicAgeDoses, int indicProfDoses,
      int indicMedicalDoses, int indicCareHomeDoses, int indicAgeFirst, int indicProfFirst, int indicMedicalFirst,
      int indicCareHomeFirst, int indicAgeFull, int indicProfFull, int indicMedicalFull, int indicCareHomeFull,
      int dosesDimCumul, int dosesKbvCumul, int dosesJohnsonCumul, int dosesBiontechFirstCumul,
      int dosesBiontechSecondCumul, int dosesModernaFirstCumul, int dosesModernaSecondCumul,
      int dosesAstraZenecaFirstCumul, int dosesAstraZenecaSecondCumul) {
    this.date = dat;
    this.dosesCumulative = dosesCumul;
    this.dosesDifferenceToPrevDay = dosesDiffPrev;
    this.dosesFirstDifferenceToPrevDay = dosesFirstDiffPrev;
    this.dosesSecondDifferenceToPrevDay = dosesSecondDiffPrev;
    this.dosesBiontechCumulative = dosesBiontechCumul;
    this.dosesModernaCumulative = dosesModernaCumul;
    this.dosesAstraZenecaCumulative = dosesAstraZenecaCumul;
    this.personsFirstCumulative = personsFirstCumul;
    this.personsFullCumulative = personsFullCumul;
    this.vaccinationRateFirst = vaccRateFirst;
    this.vaccinationRateFull = vaccRateFull;
    this.indicationAgeDoses = indicAgeDoses;
    this.indicationProfessionDoses = indicProfDoses;
    this.indicationMedicalDoses = indicMedicalDoses;
    this.indicationCareHomeDoses = indicCareHomeDoses;
    this.indicationAgeFirst = indicAgeFirst;
    this.indicationProfessionFirst = indicProfFirst;
    this.indicationMedicalFirst = indicMedicalFirst;
    this.indicationCareHomeFirst = indicCareHomeFirst;
    this.indicationAgeFull = indicAgeFull;
    this.indicationProfessionFull = indicProfFull;
    this.indicationMedicalFull = indicMedicalFull;
    this.indicationCareHomeFull = indicCareHomeFull;
    this.dosesDimCumulative = dosesDimCumul;
    this.dosesKbvCumulative = dosesKbvCumul;
    this.dosesJohnsonCumulative = dosesJohnsonCumul;
    this.dosesBiontechFirstCumulative = dosesBiontechFirstCumul;
    this.dosesBiontechSecondCumulative = dosesBiontechSecondCumul;
    this.dosesModernaFirstCumulative = dosesModernaFirstCumul;
    this.dosesModernaSecondCumulative = dosesModernaSecondCumul;
    this.dosesAstraZenecaFirstCumulative = dosesAstraZenecaFirstCumul;
    this.dosesAstraZenecaSecondCumulative = dosesAstraZenecaSecondCumul;
    this.firstHalf = new int[9];
    firstHalf[0] = this.dosesCumulative;
    firstHalf[1] = this.dosesDifferenceToPrevDay;
    firstHalf[2] = this.dosesFirstDifferenceToPrevDay;
    firstHalf[3] = this.dosesSecondDifferenceToPrevDay;
    firstHalf[4] = this.dosesBiontechCumulative;
    firstHalf[5] = this.dosesModernaCumulative;
    firstHalf[6] = this.dosesAstraZenecaCumulative;
    firstHalf[7] = this.personsFirstCumulative;
    firstHalf[8] = this.personsFullCumulative;
    this.secondHalf = new int[21];
    secondHalf[0] = this.indicationAgeDoses;
    secondHalf[1] = this.indicationProfessionDoses;
    secondHalf[2] = this.indicationMedicalDoses;
    secondHalf[3] = this.indicationCareHomeDoses;
    secondHalf[4] = this.indicationAgeFirst;
    secondHalf[5] = this.indicationProfessionFirst;
    secondHalf[6] = this.indicationMedicalFirst;
    secondHalf[7] = this.indicationCareHomeFirst;
    secondHalf[8] = this.indicationAgeFull;
    secondHalf[9] = this.indicationProfessionFull;
    secondHalf[10] = this.indicationMedicalFull;
    secondHalf[11] = this.indicationCareHomeFull;
    secondHalf[12] = this.dosesDimCumulative;
    secondHalf[13] = this.dosesKbvCumulative;
    secondHalf[14] = this.dosesJohnsonCumulative;
    secondHalf[15] = this.dosesBiontechFirstCumulative;
    secondHalf[16] = this.dosesBiontechSecondCumulative;
    secondHalf[17] = this.dosesModernaFirstCumulative;
    secondHalf[18] = this.dosesModernaSecondCumulative;
    secondHalf[19] = this.dosesAstraZenecaFirstCumulative;
    secondHalf[20] = this.dosesAstraZenecaSecondCumulative;
  }

  @Override
  public String toString() {
    // TODO
    return "{" + this.date + ", dc: " + this.dosesCumulative + "}";
  }
}