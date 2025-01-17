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

package org.optaplanner.examples.cloudbalancing.domain;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.persistence.xstream.api.score.buildin.hardsoft.HardSoftScoreXStreamConverter;

@PlanningSolution
@XStreamAlias("CloudBalance")
public class CloudBalance extends AbstractPersistable {

    private List<CloudBus> busList;

    private List<CloudRoute> routeList;

    @XStreamConverter(HardSoftScoreXStreamConverter.class)
    private HardSoftScore score;

    public CloudBalance() {
    }

    public CloudBalance(long id, List<CloudBus> computerList, List<CloudRoute> routeList) {
        super(id);
        this.busList = computerList;
        this.routeList = routeList;
    }

    @ValueRangeProvider(id = "busRange")
    @ProblemFactCollectionProperty
    public List<CloudBus> getBusList() {
        return busList;
    }

    public void setComputerList(List<CloudBus> busList) {
        this.busList = busList;
    }

    @PlanningEntityCollectionProperty
    public List<CloudRoute> getRouteList() {
        return routeList;
    }

    public void setRouteList(List<CloudRoute> routeList) {
        this.routeList = routeList;
    }

    @PlanningScore
    public HardSoftScore getScore() {
        return score;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
