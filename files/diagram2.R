suppressPackageStartupMessages(library(ggplot2))
library(reshape2)

pdfWidth <- 10 # Default 7

# Loop over all subdirectories of the result
lapply(list.dirs(recursive = F), function(dir) {
	setwd(dir)
	# Fitness
	fitnessData <- read.csv("fitness.csv")
	suppressMessages(fitnessDataM <- melt(fitnessData))
	pdf("fitness.pdf", width = pdfWidth)
	print(ggplot(data = fitnessDataM, aes(x = variable, y = value)) + geom_boxplot(aes(group = variable)))
	invisible(dev.off())
	
	# Computation time
	computationData <- read.csv("computationtime.csv")
	suppressMessages(computationDataM <- melt(computationData))
	pdf("computationTime.pdf", width = pdfWidth)
	print(ggplot(data = computationDataM, aes(x = variable, y = value)) + geom_boxplot(aes(group = variable)))
	invisible(dev.off())
	
	# Re-auctions per parcel
	nbReauctionsData <- read.csv("nbReauctions.csv")
	suppressMessages(nbReauctionsDataM <- melt(nbReauctionsData))
	pdf("nbReauctions.pdf")
	pl <- ggplot(nbReauctionsDataM, width = pdfWidth)
	print(pl + geom_histogram(aes(x = value), binwidth = 1) + facet_grid(variable ~ .))
	invisible(dev.off())
	pdf("nbReauctionsLog.pdf", width = pdfWidth)
	print(pl + geom_histogram(aes(x = value), binwidth = 1) + facet_grid(variable ~ .) + scale_y_log10())
	invisible(dev.off())
	
	# Total re-auctions
	totalReauctionsData <- read.csv("totalReauctions.csv")
	suppressMessages(totalReauctionsDataM <- melt(totalReauctionsData))
	pdf("totalReauctions.pdf", width = pdfWidth)
	print(ggplot(data = totalReauctionsDataM, aes(x = variable, y = value)) + geom_boxplot(aes(group = variable)))
	invisible(dev.off())
	
	print("Stats")
	print("Max re-auctions per parcel per strategy")
	apply(nbReauctionsData, 2, max)
	
	print("Total number of re-auctions per strategy")
	apply(totalReauctionsData, 2, sum)

	setwd("..")
})