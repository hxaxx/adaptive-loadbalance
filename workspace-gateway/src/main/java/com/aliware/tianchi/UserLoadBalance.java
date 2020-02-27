package com.aliware.tianchi;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.RpcStatus;
import org.apache.dubbo.rpc.cluster.LoadBalance;
import org.apache.dubbo.rpc.listener.CallbackListener;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author daofeng.xjf
 * <p>
 * 负载均衡扩展接口
 * 必选接口，核心接口
 * 此类可以修改实现，不可以移动类或者修改包名
 * 选手需要基于此类实现自己的负载均衡算法
 */
public class UserLoadBalance implements LoadBalance {


    static volatile RpcStatus[] statuses = new RpcStatus[3];
    static volatile double[] weight = new double[]{0.155,0.43,0.6};
    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {


        int i = invokers.size() - 1;
        double min = Double.MAX_VALUE;
        for (int t = invokers.size() - 1; t >= 0; t--) {
            double score = RpcStatus.getStatus(invokers.get(t).getUrl(),invocation.getMethodName()).getActive() * 1.0 / (weight[t]);
            if (score < min) {
                min = score;
                i = t;
            }
        }
        RpcStatus.beginCount(invokers.get(i).getUrl(), invocation.getMethodName(), 0);
        return invokers.get(i);
    }

}
