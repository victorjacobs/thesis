library(ggplot2)
library(reshape2)

# Load data
fitness <- read.csv("fitness.csv")
fitnessAvg <- melt(apply(fitness, 2, mean))

reauctions <- read.csv("totalReauctions.csv")
reauctionsAvg <- melt(apply(reauctions, 2, mean))

out <- data.frame(strategy = "curry", reauctions = reauctionsAvg$value, fitness = fitnessAvg$value)
