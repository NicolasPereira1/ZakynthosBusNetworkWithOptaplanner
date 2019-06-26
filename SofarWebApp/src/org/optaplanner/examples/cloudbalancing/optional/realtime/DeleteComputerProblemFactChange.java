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
import org.optaplanner.examples.cloudbalancing.domain.CloudRoute;

public class DeleteComputerProblemFactChange implements ProblemFactChange<CloudBalance> {

    private final CloudBus computer;

    public DeleteComputerProblemFactChange(CloudBus computer) {
        this.computer = computer;
    }

    @Override
    public void doChange(ScoreDirector<CloudBalance> scoreDirector) {
        CloudBalance cloudBalance = scoreDirector.getWorkingSolution();
        CloudBus workingComputer = scoreDirector.lookUpWorkingObject(computer);
        if (workingComputer == null) {
            // The computer has already been deleted (the UI asked to changed the same computer twice), so do nothing
            return;
        }
        // First remove the problem fact from all planning entities that use it
        for (CloudRoute process : cloudBalance.getRouteList()) {
            if (process.getBus() == workingComputer) {
                scoreDirector.beforeVariableChanged(process, "computer");
                process.setBus(null);
                scoreDirector.afterVariableChanged(process, "computer");
            }
        }
        // A SolutionCloner does not clone problem fact lists (such as computerList)
        // Shallow clone the computerList so only workingSolution is affected, not bestSolution or guiSolution
        ArrayList<CloudBus> computerList = new ArrayList<>(cloudBalance.getBusList());
        cloudBalance.setComputerList(computerList);
        // Remove the problem fact itself
        scoreDirector.beforeProblemFactRemoved(workingComputer);
        computerList.remove(workingComputer);
        scoreDirector.afterProblemFactRemoved(workingComputer);
        scoreDirector.triggerVariableListeners();
    }

}
