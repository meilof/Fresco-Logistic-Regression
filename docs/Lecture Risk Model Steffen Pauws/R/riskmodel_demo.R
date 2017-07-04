
##############
#
#  Riskmodel_demo.R
#
#  Estimate risk of dead within one year (survival) for chronic heart failure (CHF) 
#  patients from a clinical baseline measurement
#
rm(list=ls())    # will remove ALL R objects: clean sheet

library (Hmisc, T)  # Harrell’s miscellaneous library with functions useful for data analysis
library (rms)       # accompanies Harrell's book Regression Modeling Strategies
library (graphics)
library (lattice)

# load the data set
filename = "C:/My Projects/0 Population Health/Shanghai/lectures/riskmodel/R/baseline.csv"
mydata  <-  read.csv(filename)
names (mydata)

# tell R that some vars are nominal, ordinals or dates
mydata$Dead.f <- factor (mydata$Dead, exclude = NULL, ordered = FALSE)
a <-  factor(mydata$Gender)
mydata$Gender <- factor(a, levels = c(0,1), labels =c("female","male"))
mydata$Source <- factor (mydata$Source, exclude = NULL, ordered = FALSE)
mydata$Country <- factor (mydata$Country, exclude = NULL, ordered = FALSE)
mydata$Motiva <- factor (mydata$Motiva, exclude = NULL, ordered = FALSE)
levels(mydata$Motiva) <- c("usual care", "telehealth")
mydata$QoL <- factor (mydata$QoL , ordered = TRUE)
# add BMI to the set
mydata$BMI <- mydata$Weight / ( 0.0001 * mydata$Height * mydata$Height)

# list of names of variables to analyse
vars <- c ( "Dead", "Gender", "Age", "Motiva", "Height", "Weight", "BMI", 
            "SysBP", "DiaBP", "Pulse", "NTproBNP", "Creatinine", 
            "Hemoglobin", "Glucose", "QoL" )
summary (mydata[,vars])
describe (mydata[,vars])

################################
###### OUTLIER ANALYSIS ########
################################

# plot a scatterplot on sorted values, histogram or a density
# and a boxplot on dead, gender, QoL, age, height
par (mfrow = c(3,3))
plot (sort (mydata$Dead.f), pch = ".", ylab="Frequency", xlab="1Y-dead" )
plot (sort (mydata$Gender), pch = ".", ylab="Frequency", xlab="gender" )
plot (sort (mydata$QoL) , pch = ".", ylab="Frequency", xlab="QoL score" )

plot ( sort (mydata$Age), pch = ".", ylab="age(yrs)", xlab="sorted case" )
plot (density(mydata$Age, na.rm=TRUE), xlab="age(yrs)", main="age")
boxplot ( mydata$Age ~ mydata$Dead, data = mydata, xlab="1Y-dead", ylab="age(yrs)", main="" )

plot (sort (mydata$Height), pch = ".", ylab="height(cm)", xlab="sorted case" )
plot (density(mydata$Height, na.rm=TRUE), xlab="height(cm)", main="weight")
boxplot ( mydata$Height ~ mydata$Dead, data = mydata, xlab="1Y-dead", ylab="height(cm)", main="" )

# ..and the corresponding descriptive statistics
vars <- c( "Dead", "Gender", "QoL", "Age", "Height" )
summary (mydata[,vars])

# plot a scatterplot on sorted values, histogram or a density
# and a boxplot compared w/ outcome for weight, HR and SBP
par (mfrow = c(3,3))
plot (sort (mydata$Weight), pch = ".", ylab="weight(gr)", xlab="sorted case" )
plot (density(mydata$Weight, na.rm=TRUE), xlab="weight(gr)", main="height")
boxplot ( mydata$Weight ~ mydata$Dead, data = mydata, xlab="1Y-dead", ylab="weight(gr)", main="" )

plot (sort (mydata$Pulse), pch = ".", ylab="HR(bpm)", xlab="sorted case" )
plot (density(mydata$Pulse, na.rm=TRUE), xlab="HR(bpm)", main="HR")
boxplot ( mydata$Pulse ~ mydata$Dead, data = mydata, xlab="1Y-dead", ylab="HR(bpm)", main="" )

plot (sort (mydata$SysBP), pch = ".", ylab="SBP(mmHg)", xlab="sorted case" )
plot (density(mydata$SysBP, na.rm=TRUE), xlab="SBP(mmHg)", main="SBP")
boxplot ( mydata$SysBP ~ mydata$Dead, data = mydata, xlab="1Y-dead", ylab="SBP(mmHg)", main="" )

# ..and the corresponding descriptive statistics
vars <- c( "Weight", "Pulse", "SysBP" )
summary (mydata[,vars])

# plot a scatterplot on sorted values, histogram and a density
# and a boxplot compared w/ outcome for NTproBNP, Cr, glucose, Hgb
par (mfrow = c(4,3))
plot (sort (log10(mydata$NTproBNP)), pch = ".", ylab="NT-proBNP(log pg/mL)", xlab="sorted case" )
plot (density(log10(mydata$NTproBNP), na.rm=TRUE), xlab="NT-proBNP(log pg/mL)", main="NT-proBNP")
boxplot ( log10(mydata$NTproBNP) ~ mydata$Dead , data = mydata, xlab="1Y-dead", ylab="NT-proBNP(log pg/mL)", main="" )

plot (sort (mydata$Creatinine), pch = ".", ylab="Cr(log mg/dL)", xlab="sorted case" )
plot (density(mydata$Creatinine, na.rm=TRUE), xlab="Cr(log mg/dL)", main="Creatinine")
boxplot ( mydata$Creatinine ~ mydata$Dead, data = mydata, xlab="1Y-dead", ylab="Cr(log mg/dL)", main="" )

plot (sort (mydata$Glucose), pch = ".", ylab="glucose(log mmol/L)", xlab="sorted case" )
plot (density(mydata$Glucose, na.rm=TRUE), xlab="glucose(log mmol/L)", main="Glucose")
boxplot ( mydata$Glucose ~ mydata$Dead, data = mydata, xlab="1Y-dead", ylab="glucose(log mmol/L)", main="" )

plot (sort (mydata$Hemoglobin), pch = ".", ylab="Hgb(log g/dL)", xlab="sorted case" )
plot (density(mydata$Hemoglobin, na.rm=TRUE), xlab="Hgb(log g/dL)", main="Hemoglobin")
boxplot ( mydata$Hemoglobin ~ mydata$Dead, data = mydata, xlab="1Y-dead", ylab="Hgb(log g/dL)", main="" )

par (mfrow = c(1,1))  # reset of window

# ..and the corresponding descriptive statistics
vars <- c( "NTproBNP", "Creatinine", "Glucose", "Hemoglobin" )
summary (mydata[,vars])

# for age, getting min max, range and indices 
range(mydata$Age, na.rm = TRUE)
min(mydata$Age, na.rm = TRUE)
max(mydata$Age, na.rm = TRUE)
which.min(mydata$Age)
which.max(mydata$Age)

# which patients are 'non-adults' or 'antique' and what is their age
idx <- which (mydata$Age < 21 | mydata$Age >= 100)
idx
sort ( mydata$Age[idx])

# consider zero and one year of age as a missing value or simple impute them
idx <- which (mydata$Age == 0 | mydata$Age == 1)
mydata$Age[idx] <- NA
mydata$Age[idx] <- median(mydata$Age, na.rm=TRUE)

# find patients below 10 kilograms, which all happen to be zero-weight
idx <- which (mydata$Weight < 10)
idx
sort (mydata$Weight[idx])

# consider missing or impute them with median weight
idx <- which (mydata$Weight == 0)
mydata$Weight[idx] <- NA
mydata$Weight[idx] <- median(mydata$Weight, na.rm=TRUE)

# find short people shorter than 1 m and tall people longer than 2 m, 
# which all happen to be zero-length
idx <- which (mydata$Height < 100 | mydata$Height > 200)
idx
mydata$Height[idx]

# consider missing or impute them with median height
idx <- which (mydata$Height == 0)
mydata$Height[idx] <- NA
mydata$Height[idx] <- median(mydata$Height, na.rm=TRUE)

# find those with exceptional SysBP
idx <- which (mydata$SysBP < 40 | mydata$SysBP > 250)
sort (mydata$SysBP[idx])

# consider them missing or  impute them
mydata$SysBP[idx] <- NA
mydata$SysBP[idx] <- median(mydata$SysBP, na.rm=TRUE)

# find those with exceptional DiaBP
idx <- which (mydata$DiaBP < 30 | mydata$DiaBP > 250)
sort (mydata$DiaBP[idx])

# consider them missing or impute them
mydata$DiaBP[idx] <- NA
mydata$DiaBP[idx] <- median(mydata$DiaBP, na.rm=TRUE)

# find patients with extreme heart rates
idx <- which (mydata$Pulse < 35 | mydata$Pulse > 220)
sort (mydata$Pulse[idx])

# consider them missing or impute them
idx <- which (mydata$Pulse < 5 | mydata$Pulse > 220)
mydata$Pulse[idx] <- NA
mydata$Pulse[idx] <- median(mydata$Pulse, na.rm=TRUE)

# find those with exceptional NTproBNP
range (mydata$NTproBNP, na.rm = TRUE)
idx <- which(mydata$NTproBNP < 1 | mydata$NTproBNP > 100000)
sort (mydata$NTproBNP[idx])

# Creatinine
range (mydata$Creatinine, na.rm = TRUE)
idx <- which(mydata$Creatinine < 0.5 | mydata$Creatinine > 10)
sort (mydata$Creatinine[idx])

# Hgb - Hemoglobin
range (mydata$Hemoglobin, na.rm = TRUE)
idx <- which(mydata$Hemoglobin < 4.5 | mydata$Hemog > 20)
sort (mydata$Hemoglobin[idx])

# glucose
range (mydata$Glucose, na.rm = TRUE)
idx <- which (mydata$Glucose < 1| mydata$Glucose > 30)
sort (mydata$Glucose[idx])

#################################
#### MISSING VALUES ANALYSIS#####
#################################
# Cleveland dot charts
# A frequency dot plot for NAs
result  <- sapply(mydata, function(x)sum(is.na(x)))
dotplot (sort(result), main="Number of missing values", xlab = "Frequency")

# A proportional dot plot for NAs
num <- dim(mydata)[1]
result <- sapply (mydata, function(x) sum(is.na(x)) / num)
dotplot (sort (result), main="Proportion of missing values", xlab = "Proportion" )

# Clustering
vars <- c (  "Gender", "Age", "Height", "Weight", "SysBP", "DiaBP", "Pulse", "NTproBNP", 
             "Creatinine", "Hemoglobin", "Glucose", "QoL" )
result <- naclus ( mydata[,vars] )
naplot ( result, which = "mean na" )
naplot ( result, which = "na per var vs mean na" )
plot ( result, hang = .05 )


#### Recursive partitioning and dot-plot simple descriptive statistics 
#### NT-proBNP
require(rpart)
result <- rpart ( is.na( NTproBNP ) ~ Source + Country + Dead + Age + Gender + is.na(QoL), data = mydata )
plot ( result)
text ( result, cex = 0.75 )

result <- summary ( is.na (NTproBNP ) ~ Source + Country + Age + Gender + Motiva + Dead, data = mydata)
result
plot (result, cex.lab = 0.75, xlab="Missing value for NT-proBNP", main ="Missing NT-proBNP stratified for demographics and outcome", sub="" )

result <- summary ( is.na (NTproBNP ) ~ SysBP + Pulse + Height + Height + QoL, data = mydata)
result
plot (result, cex.lab = 0.75, xlab="Missing value for NT-proBNP", main ="Missing NT-proBNP stratified for measurements and self-reports", sub="")

result <- summary ( is.na (NTproBNP ) ~  Hemoglobin + Creatinine + Glucose, data = mydata)
result
plot (result, cex.lab = 0.75, xlab="Missing value for NT-proBNP", main ="Missing NT-proBNP stratified for blood tests", sub="")

##### Need to do this for QoL, Glucose, Pulse, SysBP, Height etc

###################################
#### IMPUTATION
# what number of cases is excluded in CC analysis
vars <- c ("Gender", "Age", "Motiva", "Height", "Weight", "SysBP", "Pulse", "NTproBNP", 
           "Creatinine", "Hemoglobin", "Glucose", "QoL" )
idx <- which( rowSums(is.na(mydata[,vars])) > 0)
length(idx)
length (idx) / nrow (mydata)

# what is the cumulative data set size over no. missing values per case
vars <- c ("Gender", "Age", "Motiva", "Height", "Weight", "SysBP", "Pulse", "NTproBNP", 
           "Creatinine", "Hemoglobin", "Glucose", "QoL" )
CC <- c()
for (m in c(1:(length(vars)+1))) {
  idx <- which( rowSums(is.na(mydata[,vars])) < m)
  CC[m] <- length (idx) / nrow (mydata)
}
plot( c(0:length(vars)), CC*100.0, xlab="no. missing values per case", ylab="cumulative data set size (%)", type = "b" )
CC*100.0

##### COMPLETE CASE ANALYSIS ########
# perform complete case analysis
# remove a case when it contains a missing value
vars <- c ("Dead", "Gender", "Age", "Motiva", "Height", "Weight", "SysBP", "Pulse", "NTproBNP", "Creatinine", 
           "Hemoglobin", "Glucose", "QoL" )
idx <- which( rowSums(is.na(mydata[,vars])) > 0)
CCdata <- mydata[-idx,]

# Add BMI to the data set
CCdata$BMI <- CCdata$Weight / ( 0.0001 * CCdata$Height * CCdata$Height)

summary(CCdata)
describe(CCdata)

nrow(CCdata)
sum(CCdata$Dead)
(sum(CCdata$Dead)/nrow(CCdata)) * 100.0



### UNIVARIATE TESTS
result <- summary(Dead ~ Motiva + Age + Gender + BMI + Pulse + SysBP + QoL,
                  data = CCdata)
plot ( result, cex.lab = .75, main ="", xlab="P(1Y-dead)")

result <- summary(Dead ~ log10(NTproBNP) + Creatinine + Hemoglobin + Glucose, 
                  data = CCdata )
plot ( result, cex.lab = 0.75, main ="", xlab="P(1Y-dead)" )


# create some ordinal categories for BMI, Cr, glucose and Hgb for stratification
CCdata$BMI.g[CCdata$BMI < 18.5] <- "Underweight"
CCdata$BMI.g[CCdata$BMI >= 18.5 & CCdata$BMI < 25] <- "Normal"
CCdata$BMI.g[CCdata$BMI >= 25 & CCdata$BMI < 30] <- "Overweight"
CCdata$BMI.g[CCdata$BMI >= 30 ] <- "Obese"
CCdata$BMI.g <- ordered (CCdata$BMI.g, levels = c ( "Underweight", "Normal", "Overweight", "Obese" ))

CCdata$Creatinine.g[CCdata$Creatinine < 1.0] <- "Normal Cr"
CCdata$Creatinine.g[CCdata$Creatinine >= 1.0 & CCdata$Creatinine < 2.0] <- "High Cr"
CCdata$Creatinine.g[CCdata$Creatinine >= 2.0] <- "Very high Cr"
CCdata$Creatinine.g <- ordered (CCdata$Creatinine.g, levels = c ( "Normal Cr", "High Cr", "Very high Cr" ))

CCdata$Glucose.g[CCdata$Glucose < 8] <- "Normal G"
CCdata$Glucose.g[CCdata$Glucose >= 8] <- "High G"
CCdata$Glucose.g <- ordered (CCdata$Glucose.g, levels = c ( "Normal G", "High G" ))

CCdata$Hemoglobin.g[CCdata$Hemoglobin < 12.0 ] <- "Low Hgb"
CCdata$Hemoglobin.g[CCdata$Hemoglobin >= 12.0 ] <- "Normal Hgb"
CCdata$Hemoglobin.g <- ordered (CCdata$Hemoglobin.g, levels = c ( "Low Hgb", "Normal Hgb" ))

par (mfrow = c (2,2) )      # for dividing the window in two by two
plsmo (CCdata$Age, CCdata$Dead, group=CCdata$Gender, datadensity = TRUE, ylab="P(1Y-dead)", xlab="age(yrs)" )
plsmo (CCdata$SysBP, CCdata$Dead, datadensity = TRUE, ylab="P(1Y-Dead)", xlab="SysBP(mmHg)" )
plsmo (CCdata$BMI, CCdata$Dead, datadensity = TRUE, ylab="P(1Y-Dead)", xlab="BMI(cm/kg2)" )
plsmo (CCdata$SysBP, CCdata$Dead, group=CCdata$BMI.g, datadensity = TRUE, ylab="P(1Y-Dead)", xlab="SysBP(mmHg)" )

par (mfrow = c (2,2) )      # for dividing the window in two by two
plsmo (log10(CCdata$NTproBNP), CCdata$Dead, datadensity = TRUE, ylab="P(1Y-Dead)", xlab="NT-proBNP(log pg/mL)" )
plsmo (CCdata$Creatinine, CCdata$Dead, datadensity = TRUE, ylab="P(1Y-Dead)", xlab="Creatinine(mg/dL)")
plsmo (CCdata$Glucose, CCdata$Dead, datadensity = TRUE, ylab="P(1Y-Dead)", xlab="Glucose(mmol/L)")
plsmo (CCdata$Hemoglobin, CCdata$Dead, datadensity = TRUE, ylab="P(1Y-Dead)", xlab="Hemoglobin(g/dL)")

par (mfrow = c (2,2) )      # for dividing the window in two by two
plsmo (log10(CCdata$NTproBNP), CCdata$Dead, group=CCdata$Creatinine.g, datadensity = TRUE, ylab="P(1Y-Dead)", xlab="NT-proBNP(log pg/mL)" )
plsmo (log10(CCdata$NTproBNP), CCdata$Dead, group=CCdata$Motiva, datadensity = TRUE, ylab="P(1Y-Dead)", xlab="NT-proBNP(log pg/mL)" )
plsmo (log10(CCdata$NTproBNP), CCdata$Dead, group=CCdata$Glucose.g, datadensity = TRUE, ylab="P(1Y-Dead)", xlab="NT-proBNP(log pg/mL)" )
plsmo (log10(CCdata$NTproBNP), CCdata$Dead, group=CCdata$Hemoglobin.g, datadensity = TRUE, ylab="P(1Y-Dead)", xlab="NT-proBNP(log pg/mL)" )

par (mfrow = c (1,1) )

# Multivariate risk model
# BMI >= 30 means Obese ; BMI <= 18.5 means Underweight (Normal range: 18.5 - 25)
# Pulse >= 80 bpm (80 bpm is 3rd Quartile) refers to heart palpitations, 
#               which feel like the heart is racing or throbbing.
#               To "make up for" the loss in pumping capacity, the heart beats faster.
# Creatinine >= 2.0 refers to the filtering of the kidney is deficient (kidney failure)
# Hemoglobin <= 12 refers to anemia. Reference ranges for men and women after middle age 
#                  are 12.4-14.9 g/dL and 11.7-13.8 g/dL
# Glucose >= 8.0 refers to high glucose (pre-)diabetes. 
#           Reference range fasting glucose level is 3.6 - 5.8 mmol/L
#
fit <- glm( Dead ~ Age + Gender + (BMI >= 30.0) + SysBP + (Pulse >= 80.0) + 
                   log10(NTproBNP) + (Creatinine >= 2.0) + (Hemoglobin < 12.0) + 
                   (Glucose >= 8.0) + (QoL == 0) + Motiva, family=binomial("logit"), data = CCdata )

summary ( fit )
# display regression coefficients - Beta
coefficients ( fit )
# display odds ratios
exp(coefficients (fit))

# odds ratio for age in decades
exp(coefficients (fit)[[2]])^10
# odds ratio for SysBP in 10 mmHg units
exp(coefficients (fit)[[5]])^10



###################################
# 10-fold cross validation

#######################################################
# internal validation of propensity score model
# using 10-fold cross validation
#
library(rms)     # accompanies FE Harrell's book Regression Modeling Strategies 
library(caret)   # Classification And REgression Training for predictive modelling process
library(ROCR)    # estimating and plotting performance measures over a range of cutoffs

oldw <- getOption("warn") 
options(warn = -1) # run in silent mode for demo purposes

avg_auc<-0
i<-0
while (i < 10) {
  tc <- trainControl("cv", 10, savePred=T) # apply 10 fold cross validation
  model <- train(  Dead ~ Age + Gender + (BMI >= 30.0) + SysBP + (Pulse >= 80.0) + 
                          log10(NTproBNP) + (Creatinine >= 2.0) + (Hemoglobin < 12.0) + 
                          (Glucose >= 8.0) + (QoL == 0) + Motiva,
                          data=CCdata, method="glm", trControl=tc, family=binomial)
  
  pred <- prediction(model$pred$pred, model$pred$obs)
  auc  <- performance(pred, "auc")
  auc  <- unlist(slot(auc, "y.values"))
  print ( paste ("AUC: ", round(100*auc,1), "%  "))
  avg_auc<-avg_auc+auc
  i<- i+1
}
avg_auc<- avg_auc/10
avg_auc

options(warn = oldw)  # set back original warning mode


#######################################################
# ROC analysis and performance of the risk model
# using cut-offs to find optimal decision point
#
cutoff<- 0.2425
#cutoff<- 0.1
#cutoff<- 0.5
prediction_cv <- cut(model$pred$pred, c(-Inf,cutoff,Inf), labels=c("no","yes"))
a<-table(prediction_cv,model$pred$obs)
perf <- performance(pred,"tpr","fpr") 
all<-(a[1,1]+a[1,2]+a[2,1]+a[2,2])
tp<-a[2,2]
tn<-a[1,1]
fp<-a[2,1]
fn<-a[1,2]
ppv <- tp/(tp+fp)
npv <- tn/(tn+fn)
sens <- tp/(tp+fn) 
spec <- tn/(tn+fp) 
acc <- (tp+tn)/all
prevalence <- (fn+tp)/all  
det_rate <- tp/all
det_pr <- (tp+fp)/all
e_acc <- (((tn+fp)*(tn+fn) / all) + ((fn+tp)*(fp+tp) / all)) / all 
kappa <- (acc-e_acc)/(1-e_acc)

cat(" \n- cutoff ",cutoff, 
    "  \n\npred    TRUE     FALSE\nTRUE   ",tp, "   ",fp, 
    "\nFALSE   ",fn,"   ",tn,
    "\n\nAUC              : ",round(auc,4),
    "\nAccuracy         : ",round(acc,4),
    "\nKappa            : ", round(kappa,4),
    "\n\nSensitivity      : ", round(sens,4),
    " \nSpecificity      : ", round(spec,4),
    " \nPos Pred Value   : ", round(ppv,4),
    " \nNeg Pred Value   : ", round(npv,4),
    " \nPrevalence       : ", round(prevalence,4),
    " \nDetection Rate   : ", round(det_rate,4),
    " \nDetection Prev   : ", round(det_pr,4),
    "\n\n'Positive Class' : TRUE")

## Plot ROC curve
require(pROC)
roc0 <- roc(model$pred$obs, model$pred$pred)
plot(roc0, print.thres = c(0.1, 0.2425, 0.5), type = "S", col="green",lwd=2, main="ROC Curve",
     print.thres.pattern = "%.3f (Spec = %.2f, \n Sens = %.2f)",
     print.thres.cex = .8,     legacy.axes = TRUE)



