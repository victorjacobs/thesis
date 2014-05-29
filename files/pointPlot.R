am <- melt(read.csv("averageAuctionsPerParcel.csv"))
fm <- melt(read.csv("fitness.csv"))

frame <- data.frame(auctions = am$value, fitness = fm$value)

pl <- ggplot(frame, aes(x = auctions, y = fitness))
pl + geom_point()