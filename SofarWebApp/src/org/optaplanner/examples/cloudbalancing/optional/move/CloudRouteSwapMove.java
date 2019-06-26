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

package org.optaplanner.examples.cloudbalancing.optional.move;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudBus;
import org.optaplanner.examples.cloudbalancing.domain.CloudRoute;

public class CloudRouteSwapMove extends AbstractMove<CloudBalance> {

    private CloudRoute leftCloudRoute;
    private CloudRoute rightCloudRoute;

    public CloudRouteSwapMove(CloudRoute leftCloudRoute, CloudRoute rightCloudRoute) {
        this.leftCloudRoute = leftCloudRoute;
        this.rightCloudRoute = rightCloudRoute;
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<CloudBalance> scoreDirector) {
        return !Objects.equals(leftCloudRoute.getBus(), rightCloudRoute.getBus());
    }

    @Override
    public CloudRouteSwapMove createUndoMove(ScoreDirector<CloudBalance> scoreDirector) {
        return new CloudRouteSwapMove(rightCloudRoute, leftCloudRoute);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<CloudBalance> scoreDirector) {
        CloudBus oldLeftCloudBus = leftCloudRoute.getBus();
        CloudBus oldRightCloudBus = rightCloudRoute.getBus();
        scoreDirector.beforeVariableChanged(leftCloudRoute, "bus");
        leftCloudRoute.setBus(oldRightCloudBus);
        scoreDirector.afterVariableChanged(leftCloudRoute, "bus");
        scoreDirector.beforeVariableChanged(rightCloudRoute, "bus");
        rightCloudRoute.setBus(oldLeftCloudBus);
        scoreDirector.afterVariableChanged(rightCloudRoute, "bus");
    }

    @Override
    public CloudRouteSwapMove rebase(ScoreDirector<CloudBalance> destinationScoreDirector) {
        return new CloudRouteSwapMove(destinationScoreDirector.lookUpWorkingObject(leftCloudRoute),
                destinationScoreDirector.lookUpWorkingObject(rightCloudRoute));
    }

    @Override
    public String getSimpleMoveTypeDescription() {
        return getClass().getSimpleName() + "(" + CloudRoute.class.getSimpleName() + ".bus)";
    }

    @Override
    public Collection<? extends Object> getPlanningEntities() {
        return Arrays.asList(leftCloudRoute, rightCloudRoute);
    }

    @Override
    public Collection<? extends Object> getPlanningValues() {
        return Arrays.asList(leftCloudRoute.getBus(), rightCloudRoute.getBus());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof CloudRouteSwapMove) {
            CloudRouteSwapMove other = (CloudRouteSwapMove) o;
            return new EqualsBuilder()
                    .append(leftCloudRoute, other.leftCloudRoute)
                    .append(rightCloudRoute, other.rightCloudRoute)
                    .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(leftCloudRoute)
                .append(rightCloudRoute)
                .toHashCode();
    }

    @Override
    public String toString() {
        return leftCloudRoute + " {" + leftCloudRoute.getBus() +  "} <-> "
                + rightCloudRoute + " {" + rightCloudRoute.getBus() + "}";
    }

}
