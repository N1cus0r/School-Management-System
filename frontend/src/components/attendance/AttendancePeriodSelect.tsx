import {ChangeEvent} from "react";
import {AttendancePeriod} from "../../entities/attendance/AttendancePeriod.ts";
import {Select} from "@chakra-ui/react";
import AttendanceFormatter from "../../utils/attendance-formatter.ts";

interface Props {
  value: AttendancePeriod;
  onChange: (e: ChangeEvent<HTMLSelectElement>) => void;
}

const AttendancePeriodSelect = ({ value, onChange }: Props) => {
  const attendancePeriods = Object.values(AttendancePeriod);

  return (
    <Select
      placeholder="Choose a period"
      name="period"
      value={value}
      onChange={onChange}
    >
      {attendancePeriods.map((period) => (
        <option key={period} value={period}>
          {AttendanceFormatter.getFormattedAttendancePeriod(period)}
        </option>
      ))}
    </Select>
  );
};
export default AttendancePeriodSelect;
