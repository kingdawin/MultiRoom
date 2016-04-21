# MultiRoom
wifi play music

将手机本地歌曲解码，得到解码流后，通过tcp socket传输到wifi音箱播放

用Android SDK自带的MediaCodec解码

执行流程:
获取歌曲路径->根据路径获取歌曲格式信息->
根据歌曲格式创建解码器->解码器进行解码->
分割得到的解码流->将分割的码流喂给socket发送
