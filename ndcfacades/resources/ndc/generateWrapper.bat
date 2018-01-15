@echo off
set file=NdcXsdWrapper.xsd
set path=xsds

IF "%1"=="" (SET path=xsds)

IF "%2"=="" (SET file=NdcXsdWrapper.xsd)

@echo ^<xs:schema xmlns:xs=^"http://www.w3.org/2001/XMLSchema^" xmlns=^"http://www.iata.org/IATA/EDIST^" targetNamespace=^"http://www.iata.org/IATA/EDIST^" ^> > %file%

for /r %%i in (\%path%\*.xsd) do @echo ^<xs:include schemaLocation=^"./%path%/%%~ni.xsd^" ^/^> >> %file%

@echo ^</xs:schema^> >> %file%
