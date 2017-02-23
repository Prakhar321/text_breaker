# text_breaker

                                       
1.	We made an android application which takes a given text as input and returns 10 extracted features as output 
2.	We used Api of Textprocessing.com for part-of-speech tagging, which will require internet connectivity.
3.	We could have designed our own pos tagger but it would have required lot of tagged data which we currently lack so we have used api instead. 
4.	First we have converted all the words in sentence to lower case characters 
5.	We then removed words like 'the','as','is','a','an','was','all','greater','less','than','more' etc. which were useless and doesn’t provide any actual information
6.	 For finding cc we considered 4 sub cases 
a-) for sentences of type ” cc to” or “cc’d to” we have taken following immediate nouns as cc
b-)for sentences of type “ cc’d in” we have taken immediate previous noun as cc
c-)for sentences of type ”I was cc’d ” we have taken immediate previous word as cc (note- “was” was removed in pre-processing step )
d-)for other cases we have assumed that nouns following after the word cc are cc’s 
7.	For finding from date and to date we have considered following subcases-
a-)we have taken the words like “ago”, ”last”, ”past”, ”from”, ”to” and searched and then taken corresponding CD tags as from_date and to_date   
b-)we have taken words like yesterday ,yesterday’s, today, today’s   and depending whether we have adjective “since” in sentence or not we set  from_date and to_date 
8.	For subject-
a-)we have searched word “mails” and we have taken all the immediate previous nouns as subject
b-)we have maintained a word array containing  words like 'about' , 'regarding' , 'subject' , 'subjects' , 'on' and once these words are found then we label its trailing nouns to be subject
9.	For finding sender and receiver of mail-
 a-)we have taken words like ‘from’ and ‘by’ and labeled next trailing proper noun to be sender
b-)we have taken word ‘to’ and labeled  next trailing proper noun as receiver 
10.	For has_attachment, attachment name ,attachment size ,attachment extension 
a-) we have taken searched for following extension like 'video' , 'audio' , 'videos' , 'audios' , 'pdf' ,    
                'txt' , 'ppt' , ' xml ' , 'presentation' , 'doc' ,'document', 'pdfs' , 'txts' , 'ppts' , 'xmls' , 
                'presentations' , 'docs' , 'documents' ,'text file', 'text files' 
 if anyone of the above extension is found then we label “has attachment” as “yes” 
 and label the nearest  nouns as “attachment name” 
b-)   for size we have searched for word “size” and label the next CD as size of file and we have         converted word like megabytes and kilo byte to MB and KB respectively  in pre-processing step

NOTE-Our pos tagger API  is not good enough as it is just a free service. Sometimes it gives unreliable tags as it tags proper nouns as verbs and sometimes as adjectives giving rise to unreliable results but if your name  is recognizable  and is sure to be tagged as proper noun it would give quite accurate result  




