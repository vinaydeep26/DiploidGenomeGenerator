# DiploidGenomeGenerator
This tool takes an input vcf file with phased variants along with the reference genome in fasta format and outputs A diploid Reference genome containing variants on both the alleles in an organism. 

Usage: java Task <referencefilename.fasta> <vcfFilename.vcf>  <Haplotype1Filename.fasta> <Haplotype2Filename.fasta>
it outputs two files containing the variants on each of the Alleles. be it SNVs or/and insertions/Deletions (indels).
