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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudRoute;

public class CloudRouteSwapMoveFactory implements MoveListFactory<CloudBalance> {

    @Override
    public List<CloudRouteSwapMove> createMoveList(CloudBalance cloudBalance) {
        List<CloudRoute> cloudRouteList = cloudBalance.getRouteList();
        List<CloudRouteSwapMove> moveList = new ArrayList<>();
        for (ListIterator<CloudRoute> leftIt = cloudRouteList.listIterator(); leftIt.hasNext();) {
            CloudRoute leftCloudRoute = leftIt.next();
            for (ListIterator<CloudRoute> rightIt = cloudRouteList.listIterator(leftIt.nextIndex()); rightIt.hasNext();) {
                CloudRoute rightCloudRoute = rightIt.next();
                moveList.add(new CloudRouteSwapMove(leftCloudRoute, rightCloudRoute));
            }
        }
        return moveList;
    }

}
