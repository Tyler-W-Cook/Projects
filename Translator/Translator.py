# this project will use a pretrained model and then have GluonNLP evaluate the said model
# and will allow for English to German translations
# the project will be very useful to me and my classmates because we realized we did not learn
# enough german before studying in Germany :)
# this project utilizes and builds on examples from the GluonNLP API which is linked below
# https://nlp.gluon.ai

import warnings
warnings.filterwarnings('ignore')
# disabled the warnings because there were too many

import random
import numpy as np
import mxnet as mx 
from mxnet import gluon 
import gluonnlp as nlp 

nlp.utils.check_version('0.7.0')

# setting up the enviroment with the Gluon API recommended settings 

np.random.seed(100)
random.seed(100)
mx.random.seed(10000)
ctx = mx.gpu(0) 

import nmt 

wmt_model_name = 'transformer_en_de_512'

wmt_transformer_model, wmt_src_vocab, wmt_tgt_vocab = \
    nlp.model.get_model(wmt_model_name, dataset_name='WMT2014',pretrained=True,ctx=ctx)

# we are using a model that has a mixed vocab of EN-DE, so the source and target language vocab are the same
print(len(wmt_src_vocab), len(wmt_tgt_vocab))


# processing steps 
# clip the source and target sequences via nlp.data.WMT2014BPE
# split the string input to a list of tokens
# map the string token into its index in the vocabulary
# append EOS token to source sentence and add BOS and EOS tokens to target sentence.
import hyperparameters as hparams 


wmt_data_test = nlp.data.WMT2014BPE('newstest2014', src_lang=hparams.src_lang, tgt_lang=hparams.tgt_lang)
print('Source language %s, Target language %s' % (hparams.src_lang, hparams.tgt_lang))
print('Sample BPE tokens: "{}"'.format(wmt_data_test[0]))

wmt_test_text = nlp.data.WMT2014('newstest2014',src_lang=hparams.src_lang, tgt_lang=hparams.tgt_lang)
print('Sample raw text: "{}"'.format(wmt_test_text[0]))

wmt_test_tgt_sentences = wmt_test_text.transform(lambda src, tgt: tgt)
print('Sample target sentence: "{}"'.format(wmt_test_tgt_sentences[0]))



import dataprocessor

print(dataprocessor.TrainValDataTransform.__doc__)

# wmt_transform_fn includes the four preprocessing steps mentioned above.
wmt_transform_fn = dataprocessor.TrainValDataTransform(wmt_src_vocab, wmt_tgt_vocab)
wmt_dataset_processed = wmt_data_test.transform(wmt_transform_fn, lazy=False)
print(*wmt_dataset_processed[0], sep='\n')

def get_length_index_fn():
    global idx
    idx = 0
    def transform(src, tgt):
        global idx
        result = (src, tgt, len(src), len(tgt), idx)
        idx += 1
        return result
    return transform

wmt_data_test_with_len = wmt_dataset_processed.transform(get_length_index_fn(), lazy=False)



# constructing the sampler and loaded by making the bachify function
# recommended values and datatypes
wmt_test_batchify_fn = nlp.data.batchify.Tuple(
    nlp.data.batchify.Pad(pad_val=0),
    nlp.data.batchify.Pad(pad_val=0),
    nlp.data.batchify.Stack(dtype='float32'),
    nlp.data.batchify.Stack(dtype='float32'),
    nlp.data.batchify.Stack())


# constructing a bucket sampler that compares lengths
wmt_bucket_scheme = nlp.data.ExpWidthBucket(bucket_len_step=1.2)
wmt_test_batch_sampler = nlp.data.FixedBucketSampler(
    lengths=wmt_data_test_with_len.transform(lambda src, tgt, src_len, tgt_len, idx: tgt_len), # target length
    use_average_length=True, # control the element lengths (i.e. number of tokens) to be about the same
    bucket_scheme=wmt_bucket_scheme,
    batch_size=256)
print(wmt_test_batch_sampler.stats())

# constructing a test data loader
wmt_test_data_loader = gluon.data.DataLoader(
    wmt_data_test_with_len,
    batch_sampler=wmt_test_batch_sampler,
    batchify_fn=wmt_test_batchify_fn,
    num_workers=8)
len(wmt_test_data_loader)


# constructing transformer that evakuates model the model on provided test data 
wmt_translator = nmt.translation.BeamSearchTranslator(
    model=wmt_transformer_model,
    beam_size=hparams.beam_size,
    scorer=nlp.model.BeamSearchScorer(alpha=hparams.lp_alpha, K=hparams.lp_k),
    max_length=200)


# model loss and bleu score calculation
# BLEU is an algorithm for evaluating the quality of text which has been machine-translated from one language to another.
import time
import utils

eval_start_time = time.time()

wmt_test_loss_function = nlp.loss.MaskedSoftmaxCELoss()
wmt_test_loss_function.hybridize()

wmt_detokenizer = nlp.data.SacreMosesDetokenizer()

# test loss via the utils evaluate functions
wmt_test_loss, wmt_test_translation_out = utils.evaluate(wmt_transformer_model, wmt_test_data_loader, wmt_test_loss_function, wmt_translator, wmt_tgt_vocab, wmt_detokenizer,ctx)


# bleu score calculations via nmt function
wmt_test_bleu_score, _, _, _, _ = nmt.bleu.compute_bleu([wmt_test_tgt_sentences],wmt_test_translation_out,tokenized=False, tokenizer=hparams.bleu,split_compound_word=False, bpe=False)

print('WMT14 EN-DE SOTA model test loss: %.2f; test bleu score: %.2f; time cost %.2fs'
      %(wmt_test_loss, wmt_test_bleu_score * 100, (time.time() - eval_start_time)))

print('Sample translations:')
num_pairs = 3

# translates 
for i in range(num_pairs):
    print('EN:')
    print(wmt_test_text[i][0])
    print('DE-Candidate:')
    print(wmt_test_translation_out[i])
    print('DE-Reference:')
    print(wmt_test_tgt_sentences[i])
    print('========')



# actual translation begins


print('Translate the following English sentence into German:')

sample_src_seq = 'We love language .'

print('[\'' + sample_src_seq + '\']')

sample_tgt_seq = utils.translate(wmt_translator, sample_src_seq, wmt_src_vocab, wmt_tgt_vocab, wmt_detokenizer,ctx)

print('The German translation is:')
print(sample_tgt_seq)
