library(igraph)
g <- read.graph("weighedOwnerGraph.csv", format="ncol", directed=T)
V(g)$label <- ""    # Don't need labels on vertices
E(g)$label <- E(g)$weight
E(g)$width <- 1 + 5 * (E(g)$weight / max(E(g)$weight))
E(g)$color <- rgb((E(g)$weight / max(E(g)$weight)), 1 - (E(g)$weight / max(E(g)$weight)), 0)
l <- layout.kamada.kawai(g)

pdf("weighedOwnerGraph.pdf")
plot(g, layout = l, edge.curved = 0.2)
dev.off()
