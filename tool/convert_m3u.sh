#! /bin/sh
# convert exported play list by iTunes into malarm playlist
# playlist is exported as _wakeup.m3u and _sleep.m3u

for file in sleep.m3u wakeup.m3u; do
	perl -pe 's|\r|\n|g' $file | sed -e 's|^[^#].*/||g' > _$file
done
