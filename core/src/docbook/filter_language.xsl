<?xml version='1.0' encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"> 
		
    <xsl:param name="language">en</xsl:param>
	<xsl:output doctype-public="-//OASIS//DTD DocBook XML V4.5//EN" 
				doctype-system="http://www.oasis-open.org/docbook/xml/4.5/docbookx.dtd "/>

	<xsl:template match="book">
		<book lang="{$language}" id="{@id}" revision="{@revision}">
			<xsl:apply-templates/>
		</book>
	</xsl:template>

	<xsl:template match="*">
		<xsl:if test="not(@lang) or @lang=$language">
			<xsl:choose>
				<xsl:when test="count(.//*[not(@lang) or @lang=$language])=0">
					<xsl:copy-of select="." />
				</xsl:when>
				<xsl:otherwise>
				<xsl:element name="{name(.)}">
					<xsl:for-each select="@*"><xsl:copy/></xsl:for-each>
						<xsl:apply-templates/>
					</xsl:element>	
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
			
</xsl:stylesheet>
