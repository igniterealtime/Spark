<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/">
      <html>
      <body>
        <h2>Change log</h2>

        <ul>
            <xsl:apply-templates/>
        </ul>
      </body>
      </html>
    </xsl:template>

    <xsl:template match="channel">
        <xsl:apply-templates select="item"/>
    </xsl:template>

    <xsl:template match="item">
        <li>
               <xsl:value-of select="type"/>: 
                [<xsl:value-of select="key"/>] - <xsl:value-of
                select="customfields/customfield[@id='customfield_10013']/customfieldvalues/customfieldvalue"
                    /></li>
    </xsl:template>

</xsl:stylesheet>