CREATE DATABASE DatabaseIntegrityProtectionDemo

GO

USE DatabaseIntegrityProtectionDemo

GO

CREATE TABLE Person(
	[PersonId] [int] IDENTITY NOT NULL,
	[Name] [varchar](255) NULL,
	[LastName] [varchar](255) NULL,
	[RowProtection] [varchar](550) NULL,
PRIMARY KEY (PersonId) 
)

GO

CREATE TABLE [Gallery](
	[PictureId] [int] IDENTITY NOT NULL,
	[Name] [varchar](255) NULL,
	[Picture] [varbinary](max) NULL,
	[CreationDate] [datetime] NULL,
	[NumberOfViews] [int] NULL,
	[Price] [decimal](5, 2) NULL,
	[RowProtection] [varchar](550) NULL,
PRIMARY KEY (PictureId)
) 

GO

CREATE TABLE MultipleProtections(
	Id int IDENTITY NOT NULL,	
	Regular1 varchar(255),
	Regular2 varchar(255),
	Critical1 varchar(255),
	Critical2 varchar(255),		
	RegularFieldsProtection varchar(550),
	CriticalFieldsProtection varchar(550),
	PRIMARY KEY (Id)
) 

GO


CREATE TABLE RecordVersionUpdate(
	Id int IDENTITY NOT NULL,	
	Version1Field varchar(255),
	Version2Field varchar(255),
	RecordProtection varchar(550),
	PRIMARY KEY (Id)
) 

GO

-- Inserting one record at the previous version, i.e. version 1.
INSERT [dbo].[RecordVersionUpdate] ( [Version1Field], [RecordProtection]) VALUES ( N'version 1 field in old record', N'1:1:123:1E81731910416B67A8F9F160C026FD4E1956A31AE1341A164DCC515125521462209412C6EAD1DB7A08836E7EC38D399D9F857A6E336A97BB9F42C558FC9420FDC743E6AF74EEC3230C2459A54B0DFB5CA74BC7936213CFC2D96934B947B26D7A0A4D2A3D7ADCAD0A128FB7382E4AC661DD9234DF23BD30A470D8B9203EBE3D5185FE6CA24DCC1C03BC5AB27BF5024523B28856E57D5561E4CED196E05F9EE6C651DEBB5270C7351A3EC0F35DA5E7C487B96C594F46A68E6F6A7C310751ADD27DDC933C9D852C0210CFC300449B24FBDE4225395E771EC3BCCF351CB37F4B5E6AD680B342DC527FF42EF426995054DA2B3838226AFDCE8E00F096B70DEF3C1EBA')