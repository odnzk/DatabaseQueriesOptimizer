package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.settings

import com.intellij.openapi.options.Configurable
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBPanel
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.api.SQLCommonKeywords
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.impl.breakLine.NewLinesForSpecificKeywordsSetting
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.impl.breakLine.NewLinesForSpecificKeywordsSettingUi.NEW_LINE_KEYWORDS_CLASSES_LABEL
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.impl.capitalize.KeywordCapitalizationSetting
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.impl.capitalize.KeywordCapitalizationSettingUi.CAPITALIZED_KEYWORDS_CLASSES_LABEL
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.impl.intent.IndentationForClassesSetting
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.impl.intent.IndentationForClassesSettingUi.INDENTATION_LABEL
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.impl.intent.IndentationForClassesSettingUi.INDENTED_CLASSES_LABEL
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.schema.DatabaseSchemaFieldsValidator
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.schema.DatabaseSchemaSetting
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.schema.DatabaseSchemaSettingUi.DATABASE_SCHEME_PATH_FIELD_LABEL
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.schema.DatabaseSchemaSettingUi.DATABASE_VERSION_FIELD_LABEL
import java.awt.Component
import java.awt.Dimension
import java.awt.Font
import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.ListSelectionModel
import javax.swing.text.AbstractDocument
import javax.swing.text.AttributeSet
import javax.swing.text.BadLocationException
import javax.swing.text.DocumentFilter
import kotlin.properties.Delegates


class PluginSettingsUI : Configurable {

    private val databaseSchemaFieldsValidator = DatabaseSchemaFieldsValidator()

    private var databaseDirectoryField: JTextField by Delegates.notNull()
    private var databaseVersionField: JTextField by Delegates.notNull()

    private var indentedClassesField: JBList<SQLCommonKeywords> by Delegates.notNull()
    private var indentationField: JTextField by Delegates.notNull()

    private var keywordsToBeCapitalizedField: JBList<SQLCommonKeywords> by Delegates.notNull()

    private var keywordsToBeFromNewLineField: JBList<SQLCommonKeywords> by Delegates.notNull()

    // checks whether the user has made any changes to the settings.
    override fun isModified(): Boolean {
        val savedState = PluginSettings.getInstance().state

        val databaseSettings = DatabaseSchemaSetting(
            directory = databaseDirectoryField.text.orEmpty(),
            version = databaseVersionField.text.toString().toInt(),
        )
        if (databaseSettings != savedState.databaseSchemaSetting) return true

        val indentationForClassesSetting = IndentationForClassesSetting(
            indentedClasses = indentedClassesField.selectedValuesList.toSet(),
            indentation = indentationField.text,
        )
        if (indentationForClassesSetting != savedState.indentationForClassesSetting) return true

        val keywordCapitalizationSetting = KeywordCapitalizationSetting(
            keywords = keywordsToBeCapitalizedField.selectedValuesList.toSet(),
        )
        if(keywordCapitalizationSetting != savedState.keywordCapitalizationSetting) return true

        val newLinesForSpecificKeywordsSetting = NewLinesForSpecificKeywordsSetting(
            keywords = keywordsToBeFromNewLineField.selectedValuesList.toSet(),
        )

        return newLinesForSpecificKeywordsSetting != savedState.newLinesForSpecificKeywordsSetting
    }

    // saves the settings when the user clicks "OK" or "Apply".
    override fun apply() {
        val settings = PluginSettings.getInstance()

        applyDatabaseSchemaSetting(settings)
        applyIndentationSetting(settings)
        applyKeywordsCapitalizationSetting(settings)
        applyNewLineAfterSpecificKeywordsSetting(settings)
    }

    // reloads the saved settings when the settings page is opened.
    override fun reset() = Unit


    // builds the UI components (a checkbox and text field in this case) for the settings page.
    override fun createComponent(): JComponent {
        initContent()

        return JBPanel<JBPanel<*>>().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)

            add(JLabel(DATABASE_SCHEME_PATH_FIELD_LABEL).initLabel())
            add(databaseDirectoryField)
            addSpaceBetweenItems()

            add(JLabel(DATABASE_VERSION_FIELD_LABEL).initLabel())
            add(databaseVersionField)
            addSpaceBetweenItems()

            add(JLabel(INDENTED_CLASSES_LABEL).initLabel())
            add(indentedClassesField)
            addSpaceBetweenItems()

            add(JLabel(INDENTATION_LABEL).initLabel())
            add(indentationField)
            addSpaceBetweenItems()

            add(JLabel(CAPITALIZED_KEYWORDS_CLASSES_LABEL).initLabel())
            add(keywordsToBeCapitalizedField)
            addSpaceBetweenItems()

            add(JLabel(NEW_LINE_KEYWORDS_CLASSES_LABEL).initLabel())
            add(keywordsToBeFromNewLineField)
        }
    }

    private fun JPanel.addSpaceBetweenItems() = apply {
        add(Box.createVerticalStrut(16))
    }

    private fun JLabel.initLabel() = apply {
        font = font.deriveFont(Font.BOLD)
        makeWrapContentAndPushToLeftSide()
    }

    private fun JComponent.makeWrapContentAndPushToLeftSide() = apply {
        preferredSize = Dimension(preferredSize.width, preferredSize.height)
        maximumSize = Dimension(preferredSize.width, preferredSize.height)

        alignmentX = Component.LEFT_ALIGNMENT
    }

    private fun initContent() {
        val settings = PluginSettings.getInstance().state
        databaseDirectoryField = JTextField().apply {
            text = settings.databaseSchemaSetting.directory
            makeWrapContentAndPushToLeftSide()
        }
        databaseVersionField = createDatabaseVersionField().apply {
            text = settings.databaseSchemaSetting.version.toString()
            makeWrapContentAndPushToLeftSide()
        }

        indentedClassesField = createJBList(settings.indentationForClassesSetting.indentedClasses)
        indentationField = JTextField().apply {
            text = settings.indentationForClassesSetting.indentation
            makeWrapContentAndPushToLeftSide()
        }

        keywordsToBeCapitalizedField = createJBList(settings.keywordCapitalizationSetting.keywords)

        keywordsToBeFromNewLineField = createJBList(settings.newLinesForSpecificKeywordsSetting.keywords)
    }

    private fun createJBList(selectedValues: Set<SQLCommonKeywords>): JBList<SQLCommonKeywords> =
        JBList(SQLCommonKeywords.entries).apply {
            selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
            selectedIndices = mapSelectedValuesToIndexInDisplayedList(selectedValues)
            makeWrapContentAndPushToLeftSide()
        }


    override fun getDisplayName(): String = ""

    private fun mapSelectedValuesToIndexInDisplayedList(selected: Set<SQLCommonKeywords>): IntArray {
        val result = mutableListOf<Int>()
        selected.forEach { selectedItem ->
            result.add(SQLCommonKeywords.entries.indexOf(selectedItem))
        }
        return result.toIntArray()
    }

    private fun createDatabaseVersionField(): JTextField {
        val textField = JTextField()

        val document = textField.document as AbstractDocument
        document.documentFilter = object : DocumentFilter() {
            @Throws(BadLocationException::class)
            override fun insertString(
                fb: FilterBypass,
                offset: Int,
                string: String,
                attr: AttributeSet?
            ) {
                if (string.isNumeric()) {
                    super.insertString(fb, offset, string, attr)
                }
            }

            @Throws(BadLocationException::class)
            override fun replace(
                fb: FilterBypass,
                offset: Int,
                length: Int,
                text: String,
                attrs: AttributeSet?
            ) {
                if (text.isNumeric()) {
                    super.replace(fb, offset, length, text, attrs)
                }
            }

            @Throws(BadLocationException::class)
            override fun remove(fb: FilterBypass, offset: Int, length: Int) {
                super.remove(fb, offset, length)
            }
        }

        return textField
    }

    private fun String.isNumeric(): Boolean {
        return this.all { it.isDigit() }
    }

    private fun JTextField.setupField(
        validator: (fieldText: String) -> String?,
        onSuccess: (fieldText: String) -> Unit
    ) {
        val error = validator(text)
        if (error != null) {
            border = BorderFactory.createLineBorder(JBColor.RED, 1)
            toolTipText = error
        } else {
            border = BorderFactory.createLineBorder(JBColor.GRAY, 1)
            onSuccess(text)
        }
    }

    private fun applyDatabaseSchemaSetting(settings: PluginSettings) {
        databaseDirectoryField.setupField(databaseSchemaFieldsValidator::validateDirectoryField) { text ->
            settings.loadState(
                settings.state.copy(
                    databaseSchemaSetting = settings.state.databaseSchemaSetting.copy(
                        directory = text
                    )
                )
            )
        }
        databaseVersionField.setupField(databaseSchemaFieldsValidator::validateVersionField) { text ->
            settings.loadState(
                settings.state.copy(
                    databaseSchemaSetting = settings.state.databaseSchemaSetting.copy(
                        version = text.toInt()
                    )
                )
            )
        }
    }

    private fun applyIndentationSetting(settings: PluginSettings) {
        settings.loadState(
            settings.state.copy(
                indentationForClassesSetting = IndentationForClassesSetting(
                    indentedClassesField.selectedValuesList.toSet(),
                    indentationField.text
                )

            )
        )
    }

    private fun applyKeywordsCapitalizationSetting(settings: PluginSettings) {
        settings.loadState(
            settings.state.copy(
                keywordCapitalizationSetting = KeywordCapitalizationSetting(
                    keywordsToBeCapitalizedField.selectedValuesList.toSet()
                )
            )
        )
    }

    private fun applyNewLineAfterSpecificKeywordsSetting(settings: PluginSettings){
        settings.loadState(
            settings.state.copy(
                newLinesForSpecificKeywordsSetting = NewLinesForSpecificKeywordsSetting(
                    keywordsToBeCapitalizedField.selectedValuesList.toSet()
                )
            )
        )
    }
}
