library(ggplot2)
library(reshape2)

# Disable warnings (or at least try)
warn = -1
pdfWidth <- 10 # Default 7

# Fitness
fitnessData <- read.csv("fitness.csv")
fitnessDataM <- melt(fitnessData)
pdf("fitness.pdf", width = pdfWidth)
ggplot(data = fitnessDataM, aes(x = variable, y = value)) + geom_boxplot(aes(group = variable))
dev.off()

# Computation time
computationData <- read.csv("computationtime.csv")
computationDataM <- melt(computationData)
pdf("computationTime.pdf", width = pdfWidth)
ggplot(data = computationDataM, aes(x = variable, y = value)) + geom_boxplot(aes(group = variable))
dev.off()

# Re-auctions per parcel
nbReauctionsData <- read.csv("nbReauctions.csv")
nbReauctionsDataM <- melt(nbReauctionsData)
pdf("nbReauctions.pdf")
pl <- ggplot(nbReauctionsDataM, width = pdfWidth)
pl + geom_histogram(aes(x = value), binwidth = 1) + facet_grid(variable ~ .)
dev.off()
pdf("nbReauctionsLog.pdf", width = pdfWidth)
pl + geom_histogram(aes(x = value), binwidth = 1) + facet_grid(variable ~ .) + scale_y_log10()
dev.off()

# Total re-auctions
totalReauctionsData <- read.csv("totalReauctions.csv")
totalReauctionsDataM <- melt(totalReauctionsData)
pdf("totalReauctions.pdf", width = pdfWidth)
ggplot(data = totalReauctionsDataM, aes(x = variable, y = value)) + geom_boxplot(aes(group = variable))
dev.off()

print("Stats")
print("Max re-auctions per parcel per strategy")
apply(nbReauctionsData, 2, max)

print("Total number of re-auctions per strategy")
apply(totalReauctionsData, 2, sum)
