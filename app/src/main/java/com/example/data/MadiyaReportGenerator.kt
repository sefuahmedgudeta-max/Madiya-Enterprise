package com.example.data

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object MadiyaReportGenerator {

    // Gabaasa PDF Uumuu (Guyyaa, Torban, ykn Ji'a)
    fun generateFuelReport(context: Context, inventoryList: List<FuelInventory>, reportType: String): String {
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val titlePaint = Paint()

        // Fuula PDF ijaaruu (Standard A4 Size: 595 x 842 pixels)
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        // 1. Mataduree (Header)
        titlePaint.textSize = 20f
        titlePaint.isFakeBoldText = true
        canvas.drawText("MADIYA ENTERPRISE - GABAASA ${reportType.uppercase()}", 40f, 50f, titlePaint)

        paint.textSize = 12f
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US)
        canvas.drawText("Guyyaa Uumame: ${sdf.format(Date())}", 40f, 80f, paint)
        canvas.drawText("---------------------------------------------------------------------------------", 40f, 100f, paint)

        // 2. Ragaalee Tarreeffamaa (Data Table)
        var yPosition = 130f
        canvas.drawText("Guyyaa | Bilaaloo (L) | Kan Gurgurame (L) | Kuusaa Tankaarii (L)", 40f, yPosition, paint)
        canvas.drawText("---------------------------------------------------------------------------------", 40f, yPosition + 15f, paint)
        yPosition += 35f

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        // Ragaa 15 dhumaa qofa fudhachuu
        inventoryList.take(15).forEach { data ->
            val rowText = "${dateFormat.format(Date(data.timestamp))} | ${data.bilaalooDhufe} L | ${data.kanGurgurame} L | ${data.tankariiKeessaJiru} L"
            canvas.drawText(rowText, 40f, yPosition, paint)
            yPosition += 25f
        }

        pdfDocument.finishPage(page)

        // 3. Failii Bilbila Keessatti Kuusuu (Documents Folder)
        val fileName = "Madiya_${reportType}_Report_${System.currentTimeMillis()}.pdf"
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        try {
            pdfDocument.writeTo(FileOutputStream(filePath))
            println("PDF: Gabaasni bifa PDF tiin asitti uumameera: ${filePath.absolutePath}")
        } catch (e: Exception) {
            e.printStackTrace()
            return "Gabaasa uumuu irratti dogoggorri uumame."
        } finally {
            pdfDocument.close()
        }

        return filePath.absolutePath
    }
}
