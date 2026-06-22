<?php
// 序列化
function my_serialize($obj) {
    return base64_encode(gzcompress(serialize($obj)));
}

//反序列化
function my_unserialize($txt) {
    return unserialize(gzuncompress(base64_decode($txt)));
}

$a1 = array('a' => 'Apple' ,'b' => 'banana' , 'c' => 'Coconut');
$s = my_serialize($a1);
echo "base64: {$s}\n";
$u = my_unserialize($s);
echo "反序列化结果:\n";

foreach ($u as $key => $value) {
    echo "\t{$key}: {$value}\n";
}

$a2 = array('a' => 'Apple' ,'b' => 'banana' , 'c' => 'Coconut');
// JSON序列化数组
$j = json_encode($a2);
echo "JSON: {$j}\n";
$o = json_decode($j);
echo "反序列化结果:\n";

foreach ($o as $key => $value) {
    echo "\t{$key}: {$value}\n";
}
