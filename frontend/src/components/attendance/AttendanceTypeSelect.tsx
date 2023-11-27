import { ChangeEvent } from "react";
import { Select } from "@chakra-ui/react";
import {AttendanceType} from "../../entities/attendance/AttendanceType.ts";
import StringUtils from "../../utils/string-utils.ts";

interface Props {
  value: AttendanceType;
  onChange: (e: ChangeEvent<HTMLSelectElement>) => void;
}
const AttendanceTypeSelect = ({ value, onChange }: Props) => {

  const attendanceTypes = Object.values(AttendanceType);

  return (
    <Select
      placeholder="Choose a type"
      name="type"
      value={value}
      onChange={onChange}
    >
      {attendanceTypes.map((type) => (
        <option key={type} value={type}>
          {StringUtils.getTitleString(type)}
        </option>
      ))}
    </Select>
  );
};
export default AttendanceTypeSelect;
