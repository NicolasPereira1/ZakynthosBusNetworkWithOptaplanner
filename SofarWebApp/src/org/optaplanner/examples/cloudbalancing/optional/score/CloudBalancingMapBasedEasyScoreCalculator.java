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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudBus;
import org.optaplanner.examples.cloudbalancing.domain.CloudRoute;

public class CloudBalancingMapBasedEasyScoreCalculator implements EasyScoreCalculator<CloudBalance> {

    @Override
    public HardSoftScore calculateScore(CloudBalance cloudBalance) {
        int computerListSize = cloudBalance.getBusList().size();
        Map<CloudBus, Integer> cpuPowerUsageMap = new HashMap<>(computerListSize);
        Map<CloudBus, Integer> memoryUsageMap = new HashMap<>(computerListSize);
        Map<CloudBus, Integer> networkBandwidthUsageMap = new HashMap<>(computerListSize);
        for (CloudBus computer : cloudBalance.getBusList()) {
            cpuPowerUsageMap.put(computer, 0);
            memoryUsageMap.put(computer, 0);
            networkBandwidthUsageMap.put(computer, 0);
        }
        Set<CloudBus> usedComputerSet = new HashSet<>(computerListSize);

        visitProcessList(cpuPowerUsageMap, memoryUsageMap, networkBandwidthUsageMap,
                usedComputerSet, cloudBalance.getRouteList());

        int hardScore = sumHardScore(cpuPowerUsageMap, memoryUsageMap, networkBandwidthUsageMap);
        int softScore = sumSoftScore(usedComputerSet);

        return HardSoftScore.of(hardScore, softScore);
    }

    private void visitProcessList(Map<CloudBus, Integer> cpuPowerUsageMap,
            Map<CloudBus, Integer> memoryUsageMap, Map<CloudBus, Integer> networkBandwidthUsageMap,
            Set<CloudBus> usedComputerSet, List<CloudRoute> processList) {
        // We loop through the processList only once for performance
        for (CloudRoute process : processList) {
            CloudBus computer = process.getBus();
            if (computer != null) {
                int cpuPowerUsage = cpuPowerUsageMap.get(computer) + process.getDemand();
                cpuPowerUsageMap.put(computer, cpuPowerUsage);
                int memoryUsage = memoryUsageMap.get(computer) + process.getTravelTime();
                memoryUsageMap.put(computer, memoryUsage);
                int networkBandwidthUsage = networkBandwidthUsageMap.get(computer) + process.getStartTime();
                networkBandwidthUsageMap.put(computer, networkBandwidthUsage);
                usedComputerSet.add(computer);
            }
        }
    }

    private int sumHardScore(Map<CloudBus, Integer> cpuPowerUsageMap, Map<CloudBus, Integer> memoryUsageMap,
            Map<CloudBus, Integer> networkBandwidthUsageMap) {
        int hardScore = 0;
        for (Map.Entry<CloudBus, Integer> usageEntry : cpuPowerUsageMap.entrySet()) {
            CloudBus computer = usageEntry.getKey();
            int cpuPowerAvailable = computer.getCapacity() - usageEntry.getValue();
            if (cpuPowerAvailable < 0) {
                hardScore += cpuPowerAvailable;
            }
        }
        for (Map.Entry<CloudBus, Integer> usageEntry : memoryUsageMap.entrySet()) {
            CloudBus computer = usageEntry.getKey();
            int memoryAvailable = computer.getTransfers() - usageEntry.getValue();
            if (memoryAvailable < 0) {
                hardScore += memoryAvailable;
            }
        }
        for (Map.Entry<CloudBus, Integer> usageEntry : networkBandwidthUsageMap.entrySet()) {
            CloudBus computer = usageEntry.getKey();
            int networkBandwidthAvailable = computer.getTimeFinished() - usageEntry.getValue();
            if (networkBandwidthAvailable < 0) {
                hardScore += networkBandwidthAvailable;
            }
        }
        return hardScore;
    }

    private int sumSoftScore(Set<CloudBus> usedComputerSet) {
        int softScore = 0;
        for (CloudBus usedComputer : usedComputerSet) {
            softScore -= usedComputer.getCost();
        }
        return softScore;
    }

}
