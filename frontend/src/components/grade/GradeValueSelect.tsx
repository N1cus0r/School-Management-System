import {ChangeEvent} from "react";
import {Select} from "@chakra-ui/react";

interface Props {
  value: number;
  onChange: (e: ChangeEvent<HTMLSelectElement>) => void;
}

const GradeValueSelect = ({ value, onChange }: Props) => {
  const grades = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];

  return (
    <Select
      placeholder="Choose a Grade"
      name="value"
      value={value}
      onChange={onChange}
    >
      {grades.map((grade) => (
        <option key={grade} value={grade}>
          {grade}
        </option>
      ))}
    </Select>
  );
};

export default GradeValueSelect;
