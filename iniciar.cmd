@echo off
setlocal
pushd "%~dp0GamesRun"
if errorlevel 1 (
  echo Nao foi encontrada a pasta GamesRun ao lado deste arquivo.
  pause
  exit /b 1
)
echo GamesRun Studio - projeto completo
echo Requer JDK 17 ou superior. O primeiro uso baixa as dependencias.
echo Aguarde a mensagem Started GamesRun e abra http://localhost:8081/
echo Para encerrar, pressione Ctrl+C.
echo.
call mvnw.cmd clean spring-boot:run "-Dspring-boot.run.profiles=mock"
set "GAMESRUN_EXIT=%ERRORLEVEL%"
if not "%GAMESRUN_EXIT%"=="0" (
  echo.
  echo A inicializacao falhou. Confira o PRIMEIRO erro exibido acima.
  echo Verifique o JDK, JAVA_HOME e se ja existe outro servidor na porta 8081.
  pause
)
popd
exit /b %GAMESRUN_EXIT%
