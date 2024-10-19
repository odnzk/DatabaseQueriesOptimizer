package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.ruleEngine

import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.tree.api.Query

interface RuleEngine {
    fun applyRule(query: Query): Query
}
