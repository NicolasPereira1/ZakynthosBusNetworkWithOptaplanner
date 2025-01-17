/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.cloudbalancing.optional.score;

import java.util.HashMap;
import java.util.Map;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.incremental.AbstractIncrementalScoreCalculator;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudBus;
import org.optaplanner.examples.cloudbalancing.domain.CloudRoute;

public class CloudBalancingIncrementalScoreCalculator extends AbstractIncrementalScoreCalculator<CloudBalance> {

    private Map<CloudBus, Integer> cpuPowerUsageMap;
    private Map<CloudBus, Integer> memoryUsageMap;
    private Map<CloudBus, Integer> networkBandwidthUsageMap;
    private Map<CloudBus, Integer> processCountMap;

    private int hardScore;
    private int softScore;

    @Override
    public void resetWorkingSolution(CloudBalance cloudBalance) {
        int computerListSize = cloudBalance.getBusList().size();
        cpuPowerUsageMap = new HashMap<>(computerListSize);
        memoryUsageMap = new HashMap<>(computerListSize);
        networkBandwidthUsageMap = new HashMap<>(computerListSize);
        processCountMap = new HashMap<>(computerListSize);
        for (CloudBus computer : cloudBalance.getBusList()) {
            cpuPowerUsageMap.put(computer, 0);
            memoryUsageMap.put(computer, 0);
            networkBandwidthUsageMap.put(computer, 0);
            processCountMap.put(computer, 0);
        }
        hardScore = 0;
        softScore = 0;
        for (CloudRoute process : cloudBalance.getRouteList()) {
            insert(process);
        }
    }

    @Override
    public void beforeEntityAdded(Object entity) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(Object entity) {
        // TODO the maps should probably be adjusted
        insert((CloudRoute) entity);
    }

    @Override
    public void beforeVariableChanged(Object entity, String variableName) {
        retract((CloudRoute) entity);
    }

    @Override
    public void afterVariableChanged(Object entity, String variableName) {
        insert((CloudRoute) entity);
    }

    @Override
    public void beforeEntityRemoved(Object entity) {
        retract((CloudRoute) entity);
    }

    @Override
    public void afterEntityRemoved(Object entity) {
        // Do nothing
        // TODO the maps should probably be adjusted
    }

    private void insert(CloudRoute process) {
        CloudBus computer = process.getBus();
        if (computer != null) {
            int cpuPower = computer.getCapacity();
            int oldCpuPowerUsage = cpuPowerUsageMap.get(computer);
            int oldCpuPowerAvailable = cpuPower - oldCpuPowerUsage;
            int newCpuPowerUsage = oldCpuPowerUsage + process.getDemand();
            int newCpuPowerAvailable = cpuPower - newCpuPowerUsage;
            hardScore += Math.min(newCpuPowerAvailable, 0) - Math.min(oldCpuPowerAvailable, 0);
            cpuPowerUsageMap.put(computer, newCpuPowerUsage);

            int memory = computer.getTransfers();
            int oldMemoryUsage = memoryUsageMap.get(computer);
            int oldMemoryAvailable = memory - oldMemoryUsage;
            int newMemoryUsage = oldMemoryUsage + process.getTravelTime();
            int newMemoryAvailable = memory - newMemoryUsage;
            hardScore += Math.min(newMemoryAvailable, 0) - Math.min(oldMemoryAvailable, 0);
            memoryUsageMap.put(computer, newMemoryUsage);

            int networkBandwidth = computer.getTimeFinished();
            int oldNetworkBandwidthUsage = networkBandwidthUsageMap.get(computer);
            int oldNetworkBandwidthAvailable = networkBandwidth - oldNetworkBandwidthUsage;
            int newNetworkBandwidthUsage = oldNetworkBandwidthUsage + process.getStartTime();
            int newNetworkBandwidthAvailable = networkBandwidth - newNetworkBandwidthUsage;
            hardScore += Math.min(newNetworkBandwidthAvailable, 0) - Math.min(oldNetworkBandwidthAvailable, 0);
            networkBandwidthUsageMap.put(computer, newNetworkBandwidthUsage);

            int oldProcessCount = processCountMap.get(computer);
            if (oldProcessCount == 0) {
                softScore -= computer.getCost();
            }
            int newProcessCount = oldProcessCount + 1;
            processCountMap.put(computer, newProcessCount);
        }
    }

    private void retract(CloudRoute process) {
        CloudBus computer = process.getBus();
        if (computer != null) {
            int cpuPower = computer.getCapacity();
            int oldCpuPowerUsage = cpuPowerUsageMap.get(computer);
            int oldCpuPowerAvailable = cpuPower - oldCpuPowerUsage;
            int newCpuPowerUsage = oldCpuPowerUsage - process.getDemand();
            int newCpuPowerAvailable = cpuPower - newCpuPowerUsage;
            hardScore += Math.min(newCpuPowerAvailable, 0) - Math.min(oldCpuPowerAvailable, 0);
            cpuPowerUsageMap.put(computer, newCpuPowerUsage);

            int memory = computer.getTransfers();
            int oldMemoryUsage = memoryUsageMap.get(computer);
            int oldMemoryAvailable = memory - oldMemoryUsage;
            int newMemoryUsage = oldMemoryUsage - process.getTravelTime();
            int newMemoryAvailable = memory - newMemoryUsage;
            hardScore += Math.min(newMemoryAvailable, 0) - Math.min(oldMemoryAvailable, 0);
            memoryUsageMap.put(computer, newMemoryUsage);

            int networkBandwidth = computer.getTimeFinished();
            int oldNetworkBandwidthUsage = networkBandwidthUsageMap.get(computer);
            int oldNetworkBandwidthAvailable = networkBandwidth - oldNetworkBandwidthUsage;
            int newNetworkBandwidthUsage = oldNetworkBandwidthUsage - process.getStartTime();
            int newNetworkBandwidthAvailable = networkBandwidth - newNetworkBandwidthUsage;
            hardScore += Math.min(newNetworkBandwidthAvailable, 0) - Math.min(oldNetworkBandwidthAvailable, 0);
            networkBandwidthUsageMap.put(computer, newNetworkBandwidthUsage);

            int oldProcessCount = processCountMap.get(computer);
            int newProcessCount = oldProcessCount - 1;
            if (newProcessCount == 0) {
                softScore += computer.getCost();
            }
            processCountMap.put(computer, newProcessCount);
        }
    }

    @Override
    public HardSoftScore calculateScore() {
        return HardSoftScore.of(hardScore, softScore);
    }

}
