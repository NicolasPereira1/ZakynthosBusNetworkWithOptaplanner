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

package org.optaplanner.examples.cloudbalancing.optional.realtime;

import java.util.ArrayList;

import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.ProblemFactChange;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudBus;

public class AddComputerProblemFactChange implements ProblemFactChange<CloudBalance> {

    private final CloudBus computer;

    public AddComputerProblemFactChange(CloudBus computer) {
        this.computer = computer;
    }

    @Override
    public void doChange(ScoreDirector<CloudBalance> scoreDirector) {
        CloudBalance cloudBalance = scoreDirector.getWorkingSolution();
        // Set a unique id on the new computer
        long nextComputerId = 0L;
        for (CloudBus otherComputer : cloudBalance.getBusList()) {
            if (nextComputerId <= otherComputer.getId()) {
                nextComputerId = otherComputer.getId() + 1L;
            }
        }
        computer.setId(nextComputerId);
        // A SolutionCloner does not clone problem fact lists (such as computerList)
        // Shallow clone the computerList so only workingSolution is affected, not bestSolution or guiSolution
        cloudBalance.setComputerList(new ArrayList<>(cloudBalance.getBusList()));
        // Add the problem fact itself
        scoreDirector.beforeProblemFactAdded(computer);
        cloudBalance.getBusList().add(computer);
        scoreDirector.afterProblemFactAdded(computer);
    }

}
