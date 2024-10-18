package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.tooltip

import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.Balloon.Position
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.JBColor
import com.intellij.ui.awt.RelativePoint
import java.awt.Color
import javax.swing.Icon

class SqlQueryTooltipRender(
    private val ballonIcon: Icon? = null,
    private val ballonFillColor: Color = JBColor.lightGray,
    private val ballonPosition: Position = Balloon.Position.atRight,
) {
    fun showBallon(position: RelativePoint, text: String) {
        val balloon = JBPopupFactory.getInstance()
            .createHtmlTextBalloonBuilder(text, ballonIcon, ballonFillColor, null)
            .createBalloon()
        balloon.show(position, ballonPosition)
    }
}
