public class Vaccine {
  private String name; // Name of vaccine producer, e.g. Biontech/Pfizer
  private String producerName; // Name of vaccine producer, e.g. Biontech/Pfizer
  private String vaccineType; // Type of vaccine, e.g. mRNA
  private int numberOfDoses; // number of doses for full protection
  private int columnNumberSecondDoses ; // number of column of second doses in RKI data set
  private int maximumDaysFullProtection; // max. no. of days after last dose until full protection
  private int minimumDaysFullProtection; // min. no. of days after last dose until full protection
  private int[] maximumDaysBetweenDoses; // max. no. of days after each dose until next dose
  private int[] minimumDaysBetweenDoses; // min. no. of days after each dose until next dose
  
  
  Vaccine(String vacName, String producer, String vacType, int columnNoScndDoses, int doses, int maxFull, int minFull, int[] maxDaysBtwnDoses, int[] minDaysBtwnDoses) {
    this.name = vacName;
    this.producerName = producer;
    this.vaccineType = vacType;
    this.numberOfDoses = doses;
    this.columnNumberSecondDoses = columnNoScndDoses;
    if (doses <= 0) {
      this.numberOfDoses = 1;
    }
    this.maximumDaysFullProtection = maxFull;
    this.minimumDaysFullProtection = minFull;
    if (maxFull < 0) {
      this.maximumDaysFullProtection = 0;
    }
    if (minFull < 0) {
      this.minimumDaysFullProtection = 0;
    }
    this.maximumDaysBetweenDoses = maxDaysBtwnDoses;
    this.minimumDaysBetweenDoses = minDaysBtwnDoses;
    for (int i = 0; i < maxDaysBtwnDoses.length; i++) {
      if (maxDaysBtwnDoses[i] < 0) {
        this.maximumDaysBetweenDoses[i] = 0;
      }
    }
  }

  public String getName() {
    return this.name;
  }

  public String getProducersName() {
    return this.producerName;
  }

  public String getVaccineType() {
    return this.vaccineType;
  }

  public int getNumberOfDoses() {
    return this.numberOfDoses;
  }

  public int getColumnNumberSecondDoses() {
    return this.columnNumberSecondDoses;
  }

  public int getMaximumDaysFullProtection() {
    return this.maximumDaysFullProtection;
  }

  public int getMinimumDaysFullProtection() {
    return this.minimumDaysFullProtection;
  }

  public int getMaximumDaysBetweenDoses(int beforeDose) {
    return this.maximumDaysBetweenDoses[beforeDose];
  }

  public int getMinimumDaysBetweenDoses(int beforeDose) {
    return this.minimumDaysBetweenDoses[beforeDose];
  }


  @Override
  public String toString() {
    StringBuilder valBuilder = new StringBuilder();
    valBuilder.append(this.name + ",");
    valBuilder.append(this.producerName + ",");
    valBuilder.append(this.vaccineType + ",");
    valBuilder.append(this.numberOfDoses + ",");
    valBuilder.append(this.columnNumberSecondDoses + ",");
    valBuilder.append(this.maximumDaysFullProtection + ",");
    valBuilder.append(this.minimumDaysFullProtection + ",");
    for (int i : maximumDaysBetweenDoses) {
      valBuilder.append(i + "-");
    }
    valBuilder.deleteCharAt(valBuilder.length() - 1);
    valBuilder.append(",");
    for (int i : minimumDaysBetweenDoses) {
      valBuilder.append(i + "-");
    }
    valBuilder.deleteCharAt(valBuilder.length() - 1);
    return valBuilder.toString();
  }
}
