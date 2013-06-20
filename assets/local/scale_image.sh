WIDTH=600
HEIGHT=398

SCALE_WIDTH=$(expr $WIDTH / 2)
SCALE_HEIGHT=$(expr $HEIGHT / 2)

for origin_file in $*
do
	echo converting $origin_file
	convert $origin_file -resize ${SCALE_WIDTH}x${SCALE_HEIGHT} small_$origin_file
done

