CREATE TABLE `bsn_txs_data` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `time` int(11) NOT NULL COMMENT '上链时间',
    `height` bigint(20) NOT NULL COMMENT '区块高度',
    `tx_hash` varchar(128) NOT NULL COMMENT '交易哈希',
    `status` varchar(20) NOT NULL COMMENT '状态',
    `type` varchar(255) DEFAULT NULL COMMENT '类型',
    `signers` varchar(255) DEFAULT NULL COMMENT '签名',
    `msgs` varchar(1024) DEFAULT NULL COMMENT '源码',
    `fee` varchar(255) DEFAULT NULL COMMENT '费用',
    `monikers` varchar(512) DEFAULT NULL,
    `addrs` varchar(512) DEFAULT NULL COMMENT '地址',
    `contract_addrs` varchar(512) NOT NULL COMMENT '合约地址',
    `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modify` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tx_hash` (`tx_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;