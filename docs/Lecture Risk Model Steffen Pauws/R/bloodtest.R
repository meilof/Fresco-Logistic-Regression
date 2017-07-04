#######################
#
#  bloodtest.R
#  Logistic vs linear regression examples
#
bloodvalue <- c (0, 2:37, 40)
dead <- c (0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,
           0,1,1,0,1,1,1,0,1,1,1,1,1,1,1,1,1,1,0)
bloodtest <- vector()

for (i in c(1:length(bloodvalue))) {
  bloodtest[i] <- if (bloodvalue[i] > 20) 1 else 0 } 

plot (dead ~ sort (bloodvalue), xlab = "blood value", 
      ylab = "1-Y dead", type = "p" )

# fit an ordinary linear regression: does not work
fit <- lm (dead ~ bloodvalue)
coefficients(fit)
abline(fit)

# fit a logistic regression on predictor blood test
fit <- glm (factor(dead) ~ bloodtest, family=binomial)
coefficients (fit)
exp ( coefficients ( fit ) )
lines (sort(bloodvalue), fit$fitted, type="l", col="red")

table (bloodtest, dead)
