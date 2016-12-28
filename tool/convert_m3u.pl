#!/usr/bin/perl

use strict;
use warnings;
use utf8;
use Unicode::Normalize;

use open IO => qw/:utf8 :std/;

my $content = join('', <STDIN>);
# more good code?
$content =~ s/\x0d\x0a|\x0a|\x0d/\n/g;
my @LINES = split (/\n/, $content);
foreach my $line (@LINES) {
    if ($line =~ m/^#/) {
	print $line."\n";
	next;
    }
    #$line =~ s|^.*/||;
    print NFKC($line)."\n";
}
