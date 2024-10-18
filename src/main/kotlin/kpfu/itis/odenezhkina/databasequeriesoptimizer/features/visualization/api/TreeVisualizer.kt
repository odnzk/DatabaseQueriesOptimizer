package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.visualization.api

import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.queryParser.api.SyntaxTree

interface TreeVisualizer {
    fun visualize(tree: SyntaxTree): String
}
