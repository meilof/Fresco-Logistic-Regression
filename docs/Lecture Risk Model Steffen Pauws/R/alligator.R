#####################
#
# alligator.R
# Ordinary linear regression example with 15 alligators
#
#
# 15 alligators captured in Central Florida
# length – snout vent length (from back of the head to tip of the nose) in cm
# weight – grams

alligator = data.frame (
  length = c (121.8,  93.9, 192.9,  78.4, 114.7, 117.0,  80.8,
              109.1,  84.1,  91.1, 167.7, 111.3, 103.8, 105.9, 111.3 ),
  weight = c (59112,  23091, 289873,  12672,  36213,  49871,  15020,
              40831,  16271,  17278, 165578,  38070,  36213,  37691,
              31799 )
)

plot(weight ~ length, data = alligator,
     xlab = "Snout vent length (cm)",
     ylab = "Weight (gr)",
     main = "Alligators in Central Florida"
)

# use (natural) log scale to enforce linearity
plot (log(weight) ~ log(length), data = alligator,
      xlab = "Snout vent length (cm) in natural log scale",
      ylab = "Weight (gr) in natural log scale",
      main = "Alligators in Central Florida"
)

# use ordinary linear regression with log(length) as explanatory var
# and log(weight) as response var
fit <- lm (log(weight) ~ log(length), data = alligator)
abline (fit)
summary(fit)

# check the normal distribution requirements of the residuals
plot(resid(fit) ~ fitted(fit),
     xlab = "Fitted Values",
     ylab = "Residuals",
     main = "Residual Diagnostic Plot",
     ylim = c (-0.5, 0.5)
)
grid()
abline(0.0)

qqmath( ~ resid(fit),
        xlab = "Theoretical Quantiles",
        ylab = "Residuals"
)

