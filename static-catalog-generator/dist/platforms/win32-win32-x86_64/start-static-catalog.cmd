@echo off
echo "      _        _   _                     _        _              "
echo "  ___| |_ __ _| |_(_) ___       ___ __ _| |_ __ _| | ___   __ _  "
echo " / __| __/ _` | __| |/ __|____ / __/ _` | __/ _` | |/ _ \ / _` | "
echo " \__ \ || (_| | |_| | (_|_____| (_| (_| | || (_| | | (_) | (_| | "
echo " |___/\__\__,_|\__|_|\___|     \___\__,_|\__\__,_|_|\___/ \__, | "
echo "                                                          |___/  "
echo "                                                                 "
echo Version {% version %}
SET start_path=%~dp0
start javaw -jar %start_path%/lib/static-catalog.jar
exit