# Script generating figure comparing everything
library(ggplot2)
library(reshape2)

frame <- data.frame()

for (dir in list.dirs(recursive = F)) {
	setwd(dir)
	fitness <- read.csv("fitness.csv")
	reauctions <- read.csv("totalReauctions.csv")
	newFrame <- data.frame(strategy = sub("./", "", dir), reauctions = melt(apply(reauctions, 2, mean))$value, average = melt(apply(fitness, 2, mean))$value, sd = melt(apply(fitness, 2, sd))$value)
	frame <- rbind(frame, newFrame)
	setwd('..')
}

pdf("result.pdf", width = 8)
pl <- ggplot(data = frame, aes(x = reauctions, y = average, colour = strategy))
print(pl + geom_line() + theme(legend.position = "bottom"))
invisible(dev.off())