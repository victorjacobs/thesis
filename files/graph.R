library(igraph)

lapply(list.files(pattern = ".csv", recursive = T), function(p){
  g <- read.graph(p, format="ncol", directed=T)
  if (length(V(g)) < 3) return()

  pdf(sub(".csv", ".pdf", p), width = 8)
  V(g)$label <- ""    # Don't need labels on vertices
  E(g)$label <- E(g)$weight
  E(g)$width <- 1 + 5 * (E(g)$weight / max(E(g)$weight))
  E(g)$color <- rgb((E(g)$weight / max(E(g)$weight)), 1 - (E(g)$weight / max(E(g)$weight)), 0)
  l <- layout.kamada.kawai(g)
  plot(g, layout = l, edge.curved = 0.2, edge.label.cex = 15, edge.label.font = 2, edge.label.color = "black")
  invisible(dev.off())
})
