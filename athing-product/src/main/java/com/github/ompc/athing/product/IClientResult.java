package com.github.ompc.athing.product;

/**
 * 客户端操作结果
 *
 * @param <T> 携带数据类型
 */
public interface IClientResult<T> {


    /**
     * 操作是否成功
     *
     * @return TRUE | FALSE
     */
    boolean isSuccess();

    /**
     * 平台返回结果码
     *
     * @return 返回结果码
     */
    String getErrorCode();

    /**
     * 平台返回结果信息
     *
     * @return 结果信息
     */
    String getErrorMessage();

    /**
     * 应答令牌
     *
     * @return 应答令牌
     */
    String getToken();

    /**
     * 操作结果携带数据
     *
     * @return 携带数据
     */
    T getData();

}