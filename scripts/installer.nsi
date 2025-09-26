!include MUI2.nsh

Name "Payroll App"
OutFile "..\dist\payroll-app-installer.exe"
InstallDir "$PROGRAMFILES64\PayrollApp"
ShowInstDetails show
ShowUnInstDetails show

!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_LICENSE "LICENSE.txt"
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

Var StartMenuFolder

Section "Install"
  SetOutPath "$INSTDIR"
  ; Copy packaged files into the installation directory
  ; Expect the packager to have produced dist\package
  File /r "..\dist\package\*"

  ; Create Start Menu folder
  CreateDirectory "$SMPROGRAMS\Payroll App"
  CreateShortCut "$SMPROGRAMS\Payroll App\Payroll App.lnk" "$INSTDIR\scripts\run-backend.bat"

  ; Optionally create a desktop shortcut
  CreateShortCut "$DESKTOP\Payroll App.lnk" "$INSTDIR\scripts\run-backend.bat"

  ; Write install path for uninstaller
  WriteUninstaller "$INSTDIR\uninstall.exe"
  WriteRegStr HKLM "Software\PayrollApp" "Install_Dir" "$INSTDIR"
SectionEnd

Section "Uninstall"
  Delete "$SMPROGRAMS\Payroll App\Payroll App.lnk"
  Delete "$DESKTOP\Payroll App.lnk"
  ; Remove installed files
  RMDir /r "$INSTDIR"
  ; Remove registry key
  DeleteRegKey HKLM "Software\PayrollApp"
SectionEnd
