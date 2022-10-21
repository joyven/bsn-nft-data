CREATE TABLE `bsn_txs_data`
(
    `id`             bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `time`           int(11) NOT NULL COMMENT '上链时间',
    `height`         bigint(20) NOT NULL COMMENT '区块高度',
    `tx_hash`        varchar(128) NOT NULL COMMENT '交易哈希',
    `status`         varchar(20)  NOT NULL COMMENT '状态',
    `type`           varchar(255)          DEFAULT NULL COMMENT '类型',
    `signers`        varchar(255)          DEFAULT NULL COMMENT '签名',
    `msgs`           varchar(1024)         DEFAULT NULL COMMENT '源码',
    `fee`            varchar(255)          DEFAULT NULL COMMENT '费用',
    `monikers`       varchar(512)          DEFAULT NULL,
    `addrs`          varchar(512)          DEFAULT NULL COMMENT '地址',
    `contract_addrs` varchar(512) NOT NULL COMMENT '合约地址',
    `gmt_create`     timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modify`     timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tx_hash` (`tx_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- nft交易记录
CREATE TABLE `bsn_nft_trading`
(
    `id`           bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `ntf_id`       varchar(128) COMMENT 'NFT标识',
    `ntf_name`     varchar(128) COMMENT 'NFT名称',
    `trading_hash` varchar(256) COMMENT '交易hash',
    `trading_type` varchar(128) COMMENT '交易类型',
    `category_id`  varchar(128) COMMENT '类别标识',
    `sender`       varchar(128) COMMENT '发送者',
    `recipient`    varchar(128) COMMENT '接收者',
    `amount`       decimal COMMENT '交易额',
    `height`       varchar(64) COMMENT '区块高度',
    `energy_value` bigint(20) COMMENT '能量值',
    `occur_time`   timestamp COMMENT '发生时间',
    `gmt_create`   timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modify`   timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    index          tx_hash (`trading_hash`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- nft 交易详情
CREATE TABLE `bsn_nft_info`
(
    `id`              bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `trading_hash`    varchar(256) COMMENT '交易hash',
    `trading_status`  int(1) COMMENT '交易状态 1成功 0失败',
    `height`          varchar(64) COMMENT '区块高度',
    `energy_value`    bigint(20) COMMENT '能量值',
    `remark`          varchar(128) COMMENT '备注',
    `trading_type`    varchar(128) COMMENT '交易类型',
    `category_id`     varchar(128) COMMENT '类别标识',
    `category_name`   varchar(128) COMMENT '类别名称',
    `ntf_id`          varchar(128) COMMENT 'NFT标识',
    `ntf_name`        varchar(128) COMMENT 'NFT名称',
    `sender`          varchar(128) COMMENT '发送者',
    `recipient`       varchar(128) COMMENT '接收者',
    `chain_link`      varchar(256) COMMENT '链上link',
    `chain_data`      varchar(512) COMMENT '链上数据',
    `chain_link_hash` varchar(256) COMMENT '链上link hash',
    `code`            text COMMENT '源码',
    `data_origin`     varchar COMMENT '数据源',
    `events`          text COMMENT '事件',
    `occur_time`      timestamp COMMENT '发生时间',
    `gmt_create`      timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modify`      timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    index             tx_hash (`trading_hash`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;