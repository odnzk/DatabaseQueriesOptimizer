package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.visualization.api

import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.tree.api.SqlSyntaxTree
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.visualization.impl.SqlTreeVisualizerImpl

interface SqlTreeVisualizer {
    fun visualize(tree: SqlSyntaxTree)

    companion object{
        fun create(): SqlTreeVisualizer = SqlTreeVisualizerImpl()
    }
}
