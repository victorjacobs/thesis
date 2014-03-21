library(igraph)
g <- read.graph("graph.csv", format="ncol", directed=T)
V(g)$label <- ""    # Don't need labels on vertices
E(g)$width <- 1 + 5 * (E(g)$weight / max(E(g)$weight))

plot(g)
