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

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudBus;
import org.optaplanner.examples.cloudbalancing.domain.CloudRoute;

public class CloudBusChangeMove extends AbstractMove<CloudBalance> {

    private CloudRoute cloudRoute;
    private CloudBus toCloudBus;

    public CloudBusChangeMove(CloudRoute cloudRoute, CloudBus toCloudBus) {
        this.cloudRoute = cloudRoute;
        this.toCloudBus = toCloudBus;
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<CloudBalance> scoreDirector) {
        return !Objects.equals(cloudRoute.getBus(), toCloudBus);
    }

    @Override
    public CloudBusChangeMove createUndoMove(ScoreDirector<CloudBalance> scoreDirector) {
        return new CloudBusChangeMove(cloudRoute, cloudRoute.getBus());
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<CloudBalance> scoreDirector) {
        scoreDirector.beforeVariableChanged(cloudRoute, "bus");
        cloudRoute.setBus(toCloudBus);
        scoreDirector.afterVariableChanged(cloudRoute, "bus");
    }

    @Override
    public CloudBusChangeMove rebase(ScoreDirector<CloudBalance> destinationScoreDirector) {
        return new CloudBusChangeMove(destinationScoreDirector.lookUpWorkingObject(cloudRoute),
                destinationScoreDirector.lookUpWorkingObject(toCloudBus));
    }

    @Override
    public String getSimpleMoveTypeDescription() {
        return getClass().getSimpleName() + "(" + CloudRoute.class.getSimpleName() + ".bus)";
    }

    @Override
    public Collection<? extends Object> getPlanningEntities() {
        return Collections.singletonList(cloudRoute);
    }

    @Override
    public Collection<? extends Object> getPlanningValues() {
        return Collections.singletonList(toCloudBus);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof CloudBusChangeMove) {
            CloudBusChangeMove other = (CloudBusChangeMove) o;
            return new EqualsBuilder()
                    .append(cloudRoute, other.cloudRoute)
                    .append(toCloudBus, other.toCloudBus)
                    .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(cloudRoute)
                .append(toCloudBus)
                .toHashCode();
    }

    @Override
    public String toString() {
        return cloudRoute + " {" + cloudRoute.getBus() + " -> " + toCloudBus + "}";
    }

}
