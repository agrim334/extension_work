# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

from typing import Any, Optional

from pendulum import DateTime

from airflow.timetables.base import DagRunInfo, TimeRestriction, Timetable


class NullTimetable(Timetable):
    """Timetable that never schedules anything.

    This corresponds to ``schedule_interval=None``.
    """

    def __eq__(self, other: Any) -> bool:
        """As long as *other* is of the same type."""
        if not isinstance(other, NullTimetable):
            return NotImplemented
        return True

    def validate(self) -> None:
        pass

    def next_dagrun_info(
        self,
        last_automated_dagrun: Optional[DateTime],
        restriction: TimeRestriction,
    ) -> Optional[DagRunInfo]:
        return None


class OnceTimetable(Timetable):
    """Timetable that schedules the execution once as soon as possible.

    This corresponds to ``schedule_interval="@once"``.
    """

    def __eq__(self, other: Any) -> bool:
        """As long as *other* is of the same type."""
        if not isinstance(other, OnceTimetable):
            return NotImplemented
        return True

    def validate(self) -> None:
        pass

    def next_dagrun_info(
        self,
        last_automated_dagrun: Optional[DateTime],
        restriction: TimeRestriction,
    ) -> Optional[DagRunInfo]:
        if last_automated_dagrun is not None:
            return None  # Already run, no more scheduling.
        if restriction.earliest is None:  # No start date, won't run.
            return None
        # "@once" always schedule to the start_date determined by the DAG and
        # tasks, regardless of catchup or not. This has been the case since 1.10
        # and we're inheriting it. See AIRFLOW-1928.
        run_after = restriction.earliest
        if restriction.latest is not None and run_after > restriction.latest:
            return None
        return DagRunInfo.exact(run_after)
