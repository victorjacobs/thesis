library(ggplot2)
library(reshape2)

files <- list.files(path = ".", pattern = "*.csv", full.names = F)

pdf(file = "Rplot%03d.pdf", onefile = F)

lapply(files, function(f) {
  data <- read.csv(f, header = T)
  # Melt
  m <- melt(data)
  #outFile <- paste(strsplit(f, ".csv"), ".pdf", sep = "")
  #pdf(file = outFile)

  # Plot
  ggplot(data = m, aes(x = variable, y = value)) + geom_boxplot(aes(group = variable))
  #dev.off()
})

dev.off()
