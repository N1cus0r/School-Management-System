import { Badge } from "@chakra-ui/react";

interface Props {
  dueDate: string;
}

const DueDateBadge = ({ dueDate }: Props) => {
  const date = new Date(dueDate);

  const color =
    date.getDate() > new Date().getDate() + 1
      ? "green"
      : date.getDate() > new Date().getDate()
      ? "yellow"
      : "red";

  return <Badge colorScheme={color}>{dueDate}</Badge>;
};
export default DueDateBadge;
