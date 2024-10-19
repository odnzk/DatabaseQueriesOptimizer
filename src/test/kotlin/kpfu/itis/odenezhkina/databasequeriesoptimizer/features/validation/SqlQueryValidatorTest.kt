package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.validation

import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.validation.api.SqlQueryValidator
import org.junit.Assert
import org.junit.Test

class SqlQueryValidatorTest {
    private val validator = SqlQueryValidator.create()

    @Test
    fun `GIVEN raw sql query WHEN query is valid THEN return true`(){
        val data = "SELECT * from someTable"

        val actual = validator.isSql(data)

        Assert.assertEquals(true, actual)
    }

    @Test
    fun `GIVEN raw sql query WHEN query is not valid THEN return true`(){
        val data = "SELECT * from someTable *"

        val actual = validator.isSql(data)

        Assert.assertEquals(true, actual)
    }

    @Test
    fun `GIVEN raw sql query WHEN query contains brackets THEN return true`(){
        val data = "SELECT * from sometable WHERE somecolumn='testcolumn'"

        val actual = validator.isSql(data)

        Assert.assertEquals(true, actual)
    }

    @Test
    fun `GIVEN text THEN return false`(){
        val data = "text text"

        val actual = validator.isSql(data)

        Assert.assertEquals(false, actual)
    }

    @Test
    fun `GIVEN digits THEN return false`(){
        val data = "1234581291093"

        val actual = validator.isSql(data)

        Assert.assertEquals(false, actual)
    }

    @Test
    fun `GIVEN symbols THEN return false`(){
        val data = "%$*"

        val actual = validator.isSql(data)

        Assert.assertEquals(false, actual)
    }
}