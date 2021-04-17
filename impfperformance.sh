#!/bin/bash

URL="https://impfdashboard.de/static/data/germany_vaccinations_timeseries_v2.tsv"
FILE="res/germany_vaccinations_timeseries_v2.tsv"
DAYS=7 # number of days that the vaccinations will be averaged over
TODAYSEC=$(date +%s)
POPULATIONGER=83190556 # population in germany found here: https://www.destatis.de/DE/Themen/Gesellschaft-Umwelt/Bevoelkerung/Bevoelkerungsstand/Tabellen/zensus-geschlecht-staatsangehoerigkeit-2020.html
WILLINGPERSONSPERC=62 # percentage of people wanting a vaccination found here: https://www.nordbayern.de/panorama/umfrage-zeigt-corona-impfbereitschaft-steigt-je-besser-sich-menschen-informieren-1.10829129
IMMUNITYPERC=80 # percentage of vaccines until there is a certain immunity within the population. (actual value depends, there may still be clusters and meassures needed (like wearing masks etc.))
# last lookup 2021-02-10 10:07:32 (UTC)

# get the data, link to description: https://impfdashboard.de/daten/
curl $URL > $FILE 2> /dev/null
echo "$FILE downloaded from $URL"
echo ""

# total doeses already vaccinated
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

# remove unnesseccary resource files
rm -f res/ctout1.txt
rm -f res/ctout2.txt
rm -f res/ctout3.txt
rm -f res/ctout4.txt
rm -f res/ctout5.txt
rm -f res/ctout6.txt
rm -f res/diff1.txt
rm -f res/diff2.txt
rm -f res/diff3.txt
