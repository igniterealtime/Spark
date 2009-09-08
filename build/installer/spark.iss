; -- Spark.iss --
; Demonstrates copying 3 files and creating an icon.

; SEE THE DOCUMENTATION FOR DETAILS ON CREATING .ISS SCRIPT FILES!

[Setup]
AppName=Spark
AppVerName=Spark Version  2.6.0
DefaultDirName={pf}\Spark
DefaultGroupName=Spark
UninstallDisplayIcon={app}\Spark.exe
Compression=lzma
SolidCompression=yes
OutputDir=userdocs:Spark Setup

[Files]
Source: "..\..\target\build\Spark.exe"; DestDir: "{app}"
Source: "..\..\target\build\starter.exe"; DestDir: "{app}"
Source: "..\..\target\build\lib\*"; DestDir: "{app}\lib"
Source: "..\..\target\build\lib\windows\*"; DestDir: "{app}\lib\windows\"
Source: "..\..\target\build\plugins\*"; DestDir: "{app}\plugins"
Source: "..\..\target\build\resources\*"; DestDir: "{app}\resources"
Source: "..\..\target\build\xtra\emoticons\*"; DestDir: "{app}\xtra\emoticons\"

[Icons]
Name: "{group}\Spark"; Filename: "{app}\Spark.exe"

[Registry]
Root: HKCR; Subkey: "Applications\Spark.exe"; ValueName: "TaskbarGroupIcon"; ValueData: "{app}\resources\spark.ico"

